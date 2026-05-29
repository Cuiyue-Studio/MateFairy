# MateFairy 记忆系统设计稿（端侧存储 + 远端 embedding 版）

> 状态：定稿待施工（v3，明确 embedding 走远端 API）
> 日期：2026-05-28
> 平台：PICO OS 6 (Android · ARM64) · Kotlin + Jetpack Compose · 已接入 MCP
> 部署：**存储 / 检索 / 持久化全部端侧；embedding 走远端 API（OpenAI 兼容协议）**

---

## 一、设计目标

为 MateFairy（PICO XR 上的 3D 陪伴精灵）从现有的"短期上下文 + 异步摘要"升级到**四层记忆系统**。要求：

- **存储端侧**：SQLite (Room) + filesDir，不依赖任何后端数据库 / 向量库
- **embedding 远端**：调用 OpenAI 兼容协议（智谱 / 通义 / 火山 / OpenAI 等），**包体零增量**
- **简约**：复用现有 OkHttp + Kotlin 协程，**不引 ONNX Runtime / 不引 tokenizer JNI**
- **能力对标 NAVI**：向量召回 + 事实抽取 + 矛盾检测 + 持久人设档案
- **无 cron**：用 Android 生命周期钩子（`onPause` / 空闲计时器 / 手动按钮）替代

---

## 二、四层架构总览

```
┌──────────────────────────────────────────────────────────────────┐
│  L1 ContextMemorySystem（已有）                                   │
│  滑动窗口（近 N 轮对话）+ 异步压缩长期摘要                          │
│  存储：JVM 堆内存                                                 │
└────────────────┬─────────────────────────────────────────────────┘
                 │ Consolidator：token 压力 OR 每 10 轮触发
                 ▼
┌──────────────────────────────────────────────────────────────────┐
│  L2 EpisodicStore（情节记忆）                                     │
│  会话摘要 + 事件 + 时间戳 + importance + embedding                 │
│  存储：SQLite (Room) 表 episodic + episodic_fts (FTS5)            │
│  检索：向量 cosine + BM25 + recency 三因子重排                     │
└────────────────┬─────────────────────────────────────────────────┘
                 │ FactExtractor：每轮异步抽取（shouldIngest 启发式过滤）
                 ▼
┌──────────────────────────────────────────────────────────────────┐
│  L3 SemanticStore（语义/事实记忆）                                 │
│  facts（用户偏好/技能/习惯）+ triples（KG 三元组）                  │
│  存储：SQLite (Room) 表 facts + triples                           │
│  机制：LLM 抽取 + supersede 替代删除 + 矛盾检测                    │
└────────────────┬─────────────────────────────────────────────────┘
                 │ Dream：前台空闲 5min / 手动触发
                 │      （onPause 仅触发 flush，不跑 Dream，详见第六章）
                 ▼
┌──────────────────────────────────────────────────────────────────┐
│  L4 PermanentStore（永久人设档案）                                 │
│  SOUL.md（精灵人设）/ USER.md（用户画像）/ MEMORY.md（叙事记忆）   │
│  存储：filesDir/memory/*.md + 简单备份（*.md.bak）                │
└──────────────────────────────────────────────────────────────────┘
```

| 层 | 物理存储 | 表 / 文件 |
|---|---|---|
| L1 Context | JVM 堆内存 | `ContextMemorySystem` 的 `ArrayDeque` |
| L2 Episodic | **SQLite**（Room） | `episodic`（含 embedding BLOB）+ `episodic_fts`（FTS5） |
| L3 Semantic | **SQLite**（Room） | `facts`（含 embedding）+ `triples`（KG） |
| L4 Permanent | 文件系统 | `/data/data/com.example.matefairy01/files/memory/{SOUL,USER,MEMORY}.md` |

---

## 三、关键技术栈

| 能力 | 技术 | 备注 |
|---|---|---|
| 数据库 | **SQLite via Room** | Android 原生，零依赖增量 |
| 全文索引 | **SQLite FTS5** | Room 通过 `@Fts4` / 原生 FTS5 表 |
| **Embedding 服务** | **远端 API（OpenAI 兼容协议）** | OkHttp 已有，零新增依赖；可选服务见 3.2 |
| 向量检索 | **Kotlin FloatArray 暴力 cosine** | N < 2000 时 < 100ms；规模上来再上 sqlite-vec |
| 异步队列 | **kotlinx.coroutines Channel** | 已有依赖，零新增 |
| 生命周期触发 | `Activity.onPause()` / 空闲计时器 / 手柄按键 | 不使用 WorkManager / AlarmManager |
| 版本备份 | 简单 `*.md.bak` 拷贝 | P1 不上 JGit |

**包体增量**：**< 1MB**（仅 Room runtime + 少量工具类，无模型文件）。

### 3.1 Gradle 必须改动

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    // 注意：不再需要 onnxruntime-android / djl-tokenizers（embedding 走远端 API）
}

// 根 build.gradle.kts plugins 块加：
// id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
// 然后 app 模块 plugins 块加：id("com.google.devtools.ksp")
```

> APK `noCompress` 列表与现有保持一致即可，不需要为 onnx 做特殊处理（虽然之前加了 `"onnx"` `"json"` 也无害，可保留）。

### 3.2 Embedding 服务选型（OpenAI 兼容协议）

| Provider | endpoint | 默认 model | 维度 | 中文质量 | 价格（粗估） |
|---|---|---|---|---|---|
| **智谱 BigModel** | `https://open.bigmodel.cn/api/paas/v4/embeddings` | `embedding-3` | 1024 / 2048 | 优 | ¥0.5/1M tokens |
| 阿里通义 DashScope（兼容模式） | `https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings` | `text-embedding-v3` | 1024 | 优 | ¥0.7/1M |
| 火山引擎 | `https://ark.cn-beijing.volces.com/api/v3/embeddings` | `doubao-embedding-text-240715` | 2048 | 优 | ¥0.7/1M |
| OpenAI | `https://api.openai.com/v1/embeddings` | `text-embedding-3-small` | 1536 | 良 | $0.02/1M |
| 百度千帆 | `https://qianfan.baidubce.com/v2/embeddings` | `embedding-v1` | 384 | 良 | ¥0.5/1M |
| 自建 / OpenAI 兼容代理 | 用户自填 baseUrl | 用户自填 | 用户自填 | — | — |

**陪伴对话量级低**，按千轮/天估算月费几块到几十块。

> 当前 chat completion 用 `mimo-v2.5` 走 `token-plan-cn.xiaomimimo.com`，**不一定提供 embedding 接口**，需要单独配置 embedding provider，与 chat provider **解耦**。

---

## 四、四层详细设计

### 4.1 L1 Context（保留现有）

`memory/ContextMemorySystem.kt` —— 滑动窗口 + 异步摘要。**零改动**，作为对话主流程缓冲。

### 4.2 L2 EpisodicStore（情节记忆）

#### Schema

```kotlin
@Entity(tableName = "episodic")
data class EpisodicEntry(
    @PrimaryKey val id: String,
    val content: String,             // 摘要文本
    val timestamp: Long,
    val importance: Int,             // 1-10，写入时 LLM 自评
    val embedding: ByteArray?,       // FloatArray.toBytes()，维度由 EmbeddingConfig.dimension 决定（如智谱 1024 / 通义 1024 / OpenAI 1536）
    val sourceTurnRange: String?     // "12-21"，溯源到 L1 的轮次区间
)

@Fts4(contentEntity = EpisodicEntry::class)
@Entity(tableName = "episodic_fts")
data class EpisodicFts(val content: String)
```

#### 检索：三因子重排

```kotlin
suspend fun recall(query: String, k: Int = 5): List<EpisodicEntry> {
    val qVec = embedder.encode(query)                    // 远端 API ~100-300ms

    // 时间窗预过滤（近 6 个月，限 N≤2000）
    val candidates = dao.recentSince(sixMonthsAgo)

    return candidates.map { e ->
        val sim = cosine(qVec, e.embedding)              // 暴力 cosine（端侧）
        val rec = exp(-deltaT(e.timestamp) / SEVEN_DAYS) // 半衰期 7 天
        val imp = e.importance / 10f
        e to (0.6f * sim + 0.25f * rec + 0.15f * imp)    // 三因子打分
    }.sortedByDescending { it.second }.take(k).map { it.first }
}
```

> 公式来源：Generative Agents (Park et al., Stanford, 2023) `importance × recency × relevance`。

### 4.3 L3 SemanticStore（语义/事实记忆）

#### Schema

```kotlin
// 扁平事实（覆盖 90% 陪伴场景）
@Entity(tableName = "facts")
data class Fact(
    @PrimaryKey val id: String,
    val category: String,            // preference / skill / habit / identity / dislike
    val content: String,             // "用户喜欢喝美式咖啡"
    val confidence: Float,           // 0..1
    val embedding: ByteArray,        // 语义召回用
    val supersededBy: String?,       // 替代删除：指向新事实 ID
    val createdAt: Long,
    val updatedAt: Long
)

// 三元组（关系查询，按需填充）
@Entity(tableName = "triples")
data class Triple(
    @PrimaryKey val id: String,
    val subject: String,             // "用户" / "朋友A"
    val predicate: String,           // "喜欢" / "生日是"
    val obj: String,                 // "咖啡" / "1990-01-01"
    val confidence: Float,
    val sourceEpisodeId: String?,    // 溯源到 L2
    val createdAt: Long
)
```

#### 矛盾检测（抄 NAVI）

新事实写入前，按 entity token 找近似旧事实 → 用 LLM 判定是否矛盾 → 若矛盾则用 `supersede` 标记旧事实，**不 DELETE**，保留演化历史。

#### 多跳查询（不需 Neo4j）

```kotlin
// "我之前提到的朋友A 喜欢什么?"
fun multiHop(subject: String, hops: Int = 2): List<Triple> {
    val visited = mutableSetOf<String>()
    val frontier = mutableListOf(subject)
    val result = mutableListOf<Triple>()
    repeat(hops) {
        val next = mutableListOf<String>()
        for (s in frontier) {
            if (s in visited) continue; visited.add(s)
            val triples = dao.findBySubject(s)
            result.addAll(triples)
            next.addAll(triples.map { it.obj })
        }
        frontier.clear(); frontier.addAll(next)
    }
    return result
}
```

纯 Kotlin + SQLite 索引，端侧毫秒级。

### 4.4 L4 PermanentStore（永久人设档案）

#### 文件布局

```
/data/data/com.example.matefairy01/files/memory/
├── SOUL.md         # 精灵人设、沟通风格（首次启动从 assets 拷贝模板，可手编）
├── USER.md         # 用户画像（DreamJob 从 facts 投影生成）
├── MEMORY.md       # 跨会话叙事记忆（DreamJob 从 episodic 蒸馏）
├── SOUL.md.bak
├── USER.md.bak
└── MEMORY.md.bak
```

每次 Dream 跑前先 copy `*.bak`。简单可靠，无第三方依赖。
**P2 再考虑 JGit**（包体多 2-3MB），目前用户能 ADB 手动 pull / 改 / push 即可。

---

## 五、触发机制（无 cron）

记忆系统有**三档总结**，每档触发条件不同：

| 阶段 | 干啥 | 触发条件 | 频率 | 代价 |
|---|---|---|---|---|
| ① **Consolidator** | L1 老对话 → L2 摘要 + 向量化 | **token 累计 > 3000** OR **轮数 ≥ 10** | 几分钟一次 | 1 次 LLM 摘要调用 |
| ② **FactExtractor** | 近一轮对话 → L3 facts | **每轮对话结束**（带 `shouldIngest` 启发式跳过闲聊） | 每轮（异步） | 1 次 LLM 抽取调用，可跳过 |
| ③ **Dream** | L2 + L3 → 蒸馏 L4 md | **前台空闲 5min** OR **手柄按键** | 一次会话 1-2 次 | 多次 LLM edit 调用，最贵 |
| **flush（onPause）** | 跑完挂起的 ① + ②，**不跑 Dream** | `Activity.onPause` | 摘头显 / 切后台 | 几次 LLM 调用 |

### 为什么 onPause 不跑 Dream

Android 把 Activity onPause 后几秒到几十秒就归类为 Cached App，CPU 限流甚至杀进程；DeepSeek 网络也可能被 PICO 后台策略掐。Dream 涉及多次 LLM edit（可能几分钟），**onPause 阶段跑 Dream 高概率半路死掉，留下半成品 md 文件**。

修正策略：

- **`onPause` → 仅 flush**：把已入队的 Consolidator / FactExtractor 任务跑完，**单任务超时 15s**，超时丢回队列下次再做
- **Dream → 仅前台**：用户挂机仍戴头显，触发空闲 5min 或手柄按键，前台稳定执行
- **不上 ForegroundService**：陪伴精灵不需要常驻通知栏

### 为什么不固定轮数

- **每轮跑 Dream** → LLM 调用爆炸，钱包爆炸，延迟感重
- **只看轮数** → 忽略 token 长度差异，闲聊跑早浪费、长讨论跑晚爆 context
- **混合策略最稳**：token 阈值（精确）+ 轮数兜底（保险）+ 事件触发（贴合 XR 生命周期）

### `shouldIngest` 启发式过滤（抄 NAVI）

```kotlin
fun shouldIngest(messages: List<ChatMessage>): Boolean {
    if (messages.size < 2) return false                      // 太短
    if (messages.sumOf { it.content.length } < 30) return false  // 闲聊
    if (now - lastIngestTime < 30_000) return false           // 节流
    return true
}
```

闲聊（"你好"、"哈哈"、"嗯"）不进 LLM 抽取流水线，省成本省延迟。

### 触发点代码骨架

```kotlin
// 触发 1: 用户摘下头显 / 切后台 → 仅 flush，不跑 Dream
class LaunchActivity : SpatialLaunchActivity() {
    override fun onPause() {
        super.onPause()
        val runtime = (application as SpatialApplication).runtime
        // GlobalScope 在 onPause 阶段相对安全（不要用 Activity scope，会被取消）
        applicationScope.launch {
            runtime.ingestionWorker.flush(timeoutPerJobMs = 15_000)
        }
    }
}

// 触发 2: 对话空闲 5 分钟（前台）→ 跑 Dream
class ConversationOrchestrator {
    private var lastActivity = 0L
    fun checkIdle() {
        if (now - lastActivity > 5 * 60_000) {
            scope.launch { dreamJob.run() }
        }
    }
}

// 触发 3: 每轮对话后
fun afterReply(turn: Int, tokens: Int) {
    // ① Consolidator
    if (tokens > 3000 || turn % 10 == 0) {
        ingestionWorker.enqueue(ConsolidateJob)
    }
    // ② FactExtractor
    if (shouldIngest(recentMessages)) {
        ingestionWorker.enqueue(ExtractFactsJob(recentMessages))
    }
}

// 触发 4: 手柄 Debug 按键 → 强制 Dream（开发用）
fun onDebugButtonPressed() = scope.launch { dreamJob.runForce() }
```

---

## 六、异步铁律

主对话回复**绝不等**记忆写入。

```
用户："我对花生过敏"
   ↓
ConversationOrchestrator
   ├─→ MemoryRetriever.recall()              [~150ms：远端 embed + 端侧 cosine + FTS]
   │      L2 向量召回 + L3 facts 拼 prompt
   ↓
DeepSeek API → "好的，记住了"
   ↓
精灵开口（UI 立刻返回，用户感知零延迟）
   ↓
afterReply hook（异步协程，主线程不等）：
   │
   ├─① 累计 token 检查 → Consolidator → L2 摘要入库（带 embedding）
   │
   ├─② shouldIngest 检查 → FactExtractor → L3 facts 入库
   │       发现 "花生过敏" → fact 表新增 / 矛盾检测 / supersede
   │
   └─③ 启动空闲计时器 (5min) → Dream → 蒸馏 USER.md
            USER.md 多一行: "- 对花生过敏（2026-05-28）"

onPause（用户摘头显 / 切后台）：
   └─→ flush 所有 pending IngestionJob（单任务超时 15s，超时丢回队列）
       Dream **不在此处跑**，避免 Cached App 期被杀留下半成品 md
```

`IngestionWorker` 内部 Kotlin `Channel` 串行消费，主对话路径**永远不等**。失败可重试 / 丢弃，不影响用户。

---

## 七、模块拆分（对应代码目录）

```
app/src/main/java/com/example/matefairy01/
├── ml/
│   ├── IEmbedder.kt                     # embedder 抽象，便于将来切回端侧
│   └── EmbedderRemote.kt                # OpenAI 兼容 /embeddings 远端实现，OkHttp 走通
│
├── memory/
│   ├── ContextMemorySystem.kt           # L1（保留现有）
│   │
│   ├── episodic/
│   │   ├── EpisodicEntry.kt             # @Entity
│   │   ├── EpisodicDao.kt               # @Dao
│   │   ├── EpisodicStore.kt             # 高层 API：add / recall / count
│   │   └── RecencyScorer.kt             # 时间衰减打分
│   │
│   ├── semantic/
│   │   ├── Fact.kt                      # @Entity
│   │   ├── Triple.kt                    # @Entity
│   │   ├── FactDao.kt
│   │   ├── TripleDao.kt
│   │   ├── SemanticStore.kt             # 高层 API
│   │   └── ContradictionChecker.kt      # LLM 矛盾检测
│   │
│   ├── permanent/
│   │   ├── PermanentStore.kt            # 三个 md 文件读写 + 备份
│   │   └── DreamJob.kt                  # 蒸馏：facts → USER.md，episodic → MEMORY.md
│   │
│   ├── ingestion/
│   │   ├── IngestionWorker.kt           # Channel 异步队列
│   │   ├── IngestionJob.kt              # sealed class: Consolidate / ExtractFacts
│   │   ├── Consolidator.kt              # L1 → L2 摘要
│   │   ├── FactExtractor.kt             # 对话 → L3 facts
│   │   └── ShouldIngest.kt              # 启发式过滤
│   │
│   ├── retrieval/
│   │   └── MemoryRetriever.kt           # 聚合 L2 + L3 + L4 给 prompt
│   │
│   ├── db/
│   │   ├── MateFairyDatabase.kt         # @Database(entities = [...])
│   │   └── Converters.kt                # ByteArray <-> FloatArray
│   │
│   └── MemoryConfig.kt                  # 阈值配置（token / 轮数 / 空闲）
│
└── orchestrator/
    └── ConversationOrchestrator.kt      # 已有，加 afterReply hook
```

---

## 八、配置（追加到 `app_config.json`）

```json
{
  "memory": {
    "consolidate": {
      "tokenThreshold": 3000,
      "turnFallback": 10
    },
    "factIngest": {
      "minChars": 30,
      "throttleMs": 30000
    },
    "dream": {
      "idleMs": 300000,
      "runOnPause": false,
      "maxBatchSize": 20,
      "maxIterations": 10,
      "modelOverride": null
    },
    "ingestion": {
      "flushTimeoutPerJobMs": 15000
    },
    "retrieval": {
      "weightSimilarity": 0.6,
      "weightRecency": 0.25,
      "weightImportance": 0.15,
      "recencyHalfLifeDays": 7,
      "topKEpisodic": 5,
      "topKFacts": 8
    },
    "embedding": {
      "provider": "openai_compatible",
      "baseUrl": "https://open.bigmodel.cn/api/paas/v4",
      "apiKey": "sk-your-key-here",
      "model": "embedding-3",
      "dimension": 1024,
      "maxLength": 512,
      "connectTimeoutMs": 5000,
      "readTimeoutMs": 15000,
      "batchSize": 16
    }
  }
}
```

> `embedding.dimension` **必须**与所选 model 实际输出维度严格一致（智谱 embedding-3 = 1024，OpenAI text-embedding-3-small = 1536，通义 v3 = 1024）。
> `embedding.batchSize` 是批量编码时单次请求的文本数；P1.3 Consolidator 一次入库多条时使用。

> `dream.modelOverride` 留 null 时复用主对话 model；想给 Dream 单独配置（如更便宜的 model）就填 model 名。
> `dream.runOnPause` 默认 `false`（详见第六章）。Debug 期可临时打开测试。

---

## 九、施工节奏

### P1 最小可用版（2-3 周）

| 任务 | 模块 | 备注 |
|---|---|---|
| 集成 ONNX Runtime Android | gradle | 一行依赖 |
| 转换 + 量化 bge-small-zh-v1.5 | 一次性 Python 脚本 | 输出 `assets/ml/*.onnx` + tokenizer |
| `EmbedderOnnx.kt` | ml/ | 加载 + tokenize + 推理 + mean pooling |
| Room 库 + 实体 + DAO | memory/db/ + episodic/ + semantic/ | 三张主表 + FTS |
| `IngestionWorker` + 三种 Job | memory/ingestion/ | Channel 异步 |
| `Consolidator` + `FactExtractor` | memory/ingestion/ | 调 LLM |
| `ContradictionChecker` | memory/semantic/ | 矛盾检测 + supersede |
| `MemoryRetriever` 三因子打分 | memory/retrieval/ | 聚合 L2 + L3 |
| L4 三个 md + 简单备份 | memory/permanent/ | 含模板 |
| `DreamJob` | memory/permanent/ | 蒸馏 facts → USER.md |
| 触发点接入：`onPause` / 轮数 / 空闲 / 按键 | orchestrator + activity | hook |
| `MemoryConfig` + JSON 配置 | config/ | 调试期可改 |

### P2 增强（按需）

- **JGit 版本化** L4，加 `/dream-restore` 类命令
- **sqlite-vec 扩展** 替代暴力 cosine（L2 规模 > 5000 时）
- **L3 triples 表** 真正填充，多跳查询场景验证
- 多语言 embedding 切换（如需英文场景）

### P3（最远期，按需）

- 后端服务（如果跨设备同步、家庭共享精灵等需求出现）
- HippoRAG / GraphRAG 风格的图召回

---

## 十、关键设计原则总结

1. **全端侧**：SQLite + ONNX Runtime + filesDir，零外部服务
2. **简约**：复用现有 Kotlin 协程 + Room，不引第三方向量库
3. **异步铁律**：主对话路径永不阻塞，所有写入走 `IngestionWorker`
4. **混合触发**：token 阈值 + 轮数兜底 + 生命周期事件，避开 cron
5. **可追溯**：L3 → L2 用 `sourceEpisodeId` 双向溯源；用 `supersede` 替代 DELETE
6. **可演化**：Schema 留好字段（embedding BLOB / triples 表）即使 P1 不全用，也方便 P2 平滑升级
7. **用户主权**：L4 是普通 md，用户可 ADB 拉出来看 / 改 / 备份

---

## 十一、装配与生命周期（与现有代码对接）

### 11.1 谁持有 runtime

`MateFairyRuntime` 已是依赖容器，但目前持有方未在 Activity 可访问。需要把它**升级为 Application 单例**：

```kotlin
// platform/SpatialApplication.kt（改造）
class SpatialApplication : Application() {
    lateinit var runtime: MateFairyRuntime
        private set

    override fun onCreate() {
        super.onCreate()
        SharedUIManager.initialize(this)
        val config = AppConfigLoader.load(applicationContext)
        runtime = MateFairyRuntimeFactory.create(applicationContext, config)
        applicationScope.launch {
            // Embedder 预热（首次推理冷启动 ~500ms）
            runtime.embedder.warmup()
        }
        launch(::mainApp)
    }
}
```

> `MateFairyRuntimeFactory.create` 签名要从 `(AppConfig)` 升级为 `(Context, AppConfig)`，因为 Room 数据库构建需要 `Context`。

### 11.2 Activity 怎么拿

```kotlin
// platform/LaunchActivity.kt
class LaunchActivity : SpatialLaunchActivity() {
    private val runtime get() = (application as SpatialApplication).runtime
    private val flushScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onPause() {
        super.onPause()
        flushScope.launch {
            runtime.ingestionWorker.flush(timeoutPerJobMs = 15_000)
        }
    }
}
```

> **注意**：不要用 `lifecycleScope`，Activity 进 Cached 后会取消。用 application-scope 的协程。

### 11.3 ConversationOrchestrator 改造

```kotlin
class ConversationOrchestrator(
    private val llmProvider: ILLMProvider,
    private val contextMemorySystem: ContextMemorySystem,
    private val emotionPort: EmotionCommandPort,
    private val actionPort: ActionCommandPort,
    private val ingestionWorker: IngestionWorker,        // ← 新增
    private val memoryRetriever: MemoryRetriever,        // ← 新增
    private val decisionMaker: BehaviorDecisionMaker = DefaultBehaviorDecisionMaker()
) {
    suspend fun processUserInput(text: String): ConversationResult {
        contextMemorySystem.addMessage(ChatMessage(role = "user", content = text))

        // ↓ 改造：build 时注入 L2/L3/L4 召回结果
        val messages = contextMemorySystem.buildPromptMessages(query = text, retriever = memoryRetriever)

        val response = llmProvider.chat(messages)
        contextMemorySystem.addMessage(ChatMessage(role = "assistant", content = response.reply_text))

        // ↓ 新增：异步入队（不等待）
        ingestionWorker.enqueueAfterReply(text, response.reply_text)

        // 决策 + 分发（保留原逻辑）
        val decision = decisionMaker.decide(...)
        if (decision.shouldTriggerEmotion) emotionPort.triggerEmotion(decision.resolvedEmotion)
        if (decision.shouldDispatchAction) coroutineScope { launch { actionPort.dispatchAction(decision.resolvedActionIntent) } }

        return ConversationResult(replyText = decision.overriddenReplyText ?: response.reply_text)
    }
}
```

### 11.4 ContextMemorySystem.buildPromptMessages 改造

```kotlin
suspend fun buildPromptMessages(
    query: String? = null,
    retriever: MemoryRetriever? = null
): List<ChatMessage> {
    val messages = mutableListOf<ChatMessage>()

    // 1. SOUL.md（永久人设，注入到 system prompt）
    val soulContent = retriever?.permanentStore?.readSoul().orEmpty()
    val systemPrompt = if (soulContent.isNotBlank()) {
        "${llmProvider.systemPrompt}\n\n# 你的设定\n$soulContent"
    } else llmProvider.systemPrompt
    messages += ChatMessage(role = "system", content = systemPrompt)

    // 2. USER.md（用户画像）
    retriever?.permanentStore?.readUser()?.takeIf { it.isNotBlank() }?.let {
        messages += ChatMessage(role = "system", content = "# 关于这位用户\n$it")
    }

    // 3. L2/L3 召回（按当前 query）
    if (query != null && retriever != null) {
        val recall = retriever.assembleContext(query)        // 含 episodic + facts
        if (recall.isNotBlank()) {
            messages += ChatMessage(role = "system", content = "# 相关回忆\n$recall")
        }
    }

    // 4. 长期摘要（保留现有）
    if (longTermMemorySummary.isNotEmpty()) {
        messages += ChatMessage(role = "system", content = "# 历史摘要\n$longTermMemorySummary")
    }

    // 5. 最近对话（保留现有）
    messages += shortTermMemory
    return messages
}
```

---

## 十二、已知限制 / 已识别风险

| 项 | 影响 | 缓解 |
|---|---|---|
| **FTS5 中文分词差** | Room `@Fts4` 默认 unicode61 对中文按字符切，召回质量弱 | P1 不依赖 FTS 召回质量；主排序靠向量 cosine + recency + importance；FTS 仅做候选生成与排序加权，权重低；P2 再考虑 ICU / Jieba |
| **远端 embedding 网络不稳** | 对话首字延迟 +100-300ms，断网时 recall 降级 | OkHttp 设短 timeout（5s connect / 15s read）；失败返回零向量（cosine 全 0 → 仅靠 recency / importance 排序）；P2 缓存常用 query embedding |
| **远端 embedding 服务下线 / API key 失效** | 召回质量打折但不崩 | `IEmbedder` 接口抽象；可一键切到备用 provider；零向量降级保底 |
| **维度配置错位** | embed 维度与 DB 已存向量不匹配 → cosine 出错 | 启动断言 `embedder.dimension == config.embedding.dimension`；运行时校验向量长度 |
| **embedding 数据上传第三方** | 用户对话内容（节选）发往 embedding 服务 | 选用国内合规 provider（智谱 / 阿里 / 火山）；向用户告知 |
| **SpatialLaunchActivity 生命周期** | `LaunchActivity : SpatialLaunchActivity()`，PICO SDK 是否拦截 onPause 未知 | P1.5 落 logcat 验证；99% 没问题；1% 风险若被吞，改用 `ProcessLifecycleOwner` 监听 ON_PAUSE |
| **DeepSeek / mimo API 在后台被掐** | PICO 后台策略可能限制网络 | onPause 仅 flush（短时），Dream 仅前台跑，规避大部分场景 |
| **Room schema 演化** | P1 无 migration，schema 改 = 数据丢 | P1 期破坏性更新可接受；P2 引入 `@Database(version = 2, autoMigrations = ...)` |
| **LLM 矛盾检测误判** | 可能错误 supersede 正确事实 | 用 supersede 软标记不删除；用户可 ADB 改 USER.md 兜底 |
| **API token 成本** | embedding + chat 双调用 | 陪伴量级低，月费几元到几十元；ShouldIngest 启发式严格过滤减少抽取调用 |

---

## 十三、参考资料

- **nanobot 记忆设计**：`D:\xr\nanobot\docs\memory.md`（纯 md+jsonl+git，最简哲学）
- **NAVI 记忆架构**：`D:\xr\NAVI\backend\memory\README.md`（混合方案，桌面 Python 栈，本设计的端侧对标）
- **MateFairy 现状**：`D:\xr\MateFairy\PROJECT_SUMMARY.md`

### 理论根基

- Atkinson-Shiffrin 多存储模型（1968）：感觉/短期/长期分层
- Tulving (1972, 1985)：Episodic vs Semantic 分离 → L2 / L3 拆法
- Generative Agents (Park et al., Stanford, 2023)：`importance × recency × relevance` 三因子检索 → L2 公式
- MemGPT (Packer et al., 2023)：分层记忆有效性
- Mem0 (2024-2025)：extract → update → consolidate 流水线 → IngestionWorker 思路
- NAVI：`shouldIngest` 启发式 + `supersede` 矛盾检测的工业实践

> 注：2026 年最新 arXiv 论文未联网核对（API 限速 + 工具受限），后续可补查。
