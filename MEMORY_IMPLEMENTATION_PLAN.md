# MateFairy 记忆系统实施计划

> 状态：施工指导稿（v3，embedding 改为远端 API）
> 日期：2026-05-28
> 配套设计稿：`MEMORY_SYSTEM_DESIGN.md`（v3）
> 范围：从零落地端侧四层记忆系统，分阶段、可验证

---

## ⚠️ v3 修订说明

| 变更 | 原因 |
|---|---|
| **embedding 由本地 ONNX 改为远端 API**（OpenAI 兼容协议） | "全端侧"在本项目的语义 = 存储 / 检索端侧；embedding 服务调用远端 API 反而更轻 |
| 包体增量 ~35MB → **< 1MB** | 不再打包 ONNX 模型 + tokenizer + ONNX Runtime |
| 删除依赖：`onnxruntime-android` / `djl-tokenizers` | 改用 OkHttp（已有） |
| 删除任务：模型导出脚本、INT8 量化、JNI 实测 | 不再需要 |
| 新增字段：`embedding.baseUrl` / `apiKey` / `model` / `provider` | OpenAI 兼容协议参数 |
| 新增风险：网络不稳、API key 失效、数据合规 | 见风险登记册 |
| `noCompress` 加 `"onnx"` `"json"` 已生效 | 保留无害，留作未来本地 fallback 备份方案 |

## 历史保留：v2 修补的 6 处漏洞（依然适用）

| # | 漏洞 | 修补 | 影响阶段 |
|---|---|---|---|
| 1 | embedding 维度需要严格对齐 | `EmbeddingConfig.dimension` 与 provider 实际输出严格一致；启动断言 | P1.0 |
| 2 | APK 压缩列表加 `.onnx`、`.json` | 已加，保留作 P2 本地 fallback 备份 | 已完成 |
| 3 | onPause → Dream 时序问题 | onPause **只 flush 不 Dream**；Dream 仅前台 idle 跑 | P1.5 |
| 4 | runtime 在 Activity 不可达 | `SpatialApplication` 持有 `runtime` 单例 | P1.5 |
| 5 | FTS5 中文分词差 | 主排序靠向量 cosine，FTS 仅作候选生成；权重低 | P1.2（设计已对齐） |
| 6 | ~~Tokenizer JNI 未实测~~ | **v3 移除**：embedding 走远端，无 JNI 依赖 | — |

---

## 一、Codebase 审计结论

施工前先把现状摸清，确定每个模块挂哪里、改哪里。

### 1.1 现有结构（已有）

| 文件 | 角色 | 与记忆系统的关系 |
|---|---|---|
| `runtime/MateFairyRuntimeFactory.kt` | 集中依赖装配 | **新模块全部挂这里**，避免散装 |
| `runtime/MateFairyRuntime.kt` | 运行时容器 | 暴露记忆系统给上层（HomeStage 等） |
| `orchestrator/ConversationOrchestrator.kt` | 唯一对话编排入口 | **唯一 hook 点**：`processUserInput` 末尾追加 ingestion |
| `memory/ContextMemorySystem.kt` | L1 滑动窗 + 异步摘要 | **保留**，作为 L1；`buildPromptMessages` 改造为支持 L2/L3 注入 |
| `ai/ILLMProvider.kt` | LLM 抽象（chat / summarize） | 直接复用调 LLM 抽事实 / 蒸馏 md |
| `ai/DeepSeekLLMProvider.kt` | 真实 LLM | 同上 |
| `config/AppConfig.kt` + `AppConfigLoader` | JSON 配置加载 | **加 `MemoryConfig` 节点 + parser** |
| `platform/SpatialApplication.kt` | Application 入口 | 启动时初始化 Embedder / Database |
| `platform/LaunchActivity.kt` | 主 Activity（继承 SpatialLaunchActivity） | **覆写 `onPause` 触发 flush + Dream** |
| `mcp/McpManager.kt` | MCP 工具聚合 | P2/P3 暴露 memory 工具时复用 |

### 1.2 缺失依赖（Gradle 需新增）

```kotlin
// app/build.gradle.kts dependencies 块
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")          // 协程 + Flow
ksp("androidx.room:room-compiler:2.6.1")                // Annotation processor
implementation("androidx.lifecycle:lifecycle-process:2.8.7")  // ProcessLifecycleOwner
// embedding 走远端 API → OkHttp 已有，不需新增
```

需在根 `build.gradle.kts` 启用 `com.google.devtools.ksp` plugin（`2.0.0-1.0.21` 与项目 Kotlin 2.0.0 配套）。

### 1.2.1 APK 资源压缩配置

`noCompress` 加 `"onnx"` 与 `"json"` 已在前期施工完成。即使 v3 不再打包 ONNX 模型，**保留无害**（未来若回切本地 fallback 直接复用）。

### 1.3 关键改造点

| 改造 | 位置 | 描述 |
|---|---|---|
| **A. `ContextMemorySystem.buildPromptMessages`** | `memory/ContextMemorySystem.kt` | 重构成 `suspend fun build(query: String, retriever: MemoryRetriever): List<ChatMessage>`，在 system prompt 后插入：(1) L4 SOUL/USER 内容；(2) L2 三因子召回 top-K；(3) L3 相关 facts |
| **B. `ConversationOrchestrator.processUserInput`** | `orchestrator/ConversationOrchestrator.kt` | 末尾追加 `ingestionWorker.enqueueAfterReply(...)`；调用 `build(text, retriever)` 替代 `buildPromptMessages` |
| **C. `MateFairyRuntimeFactory.create`** | `runtime/MateFairyRuntimeFactory.kt` | **签名升级为 `create(context: Context, appConfig: AppConfig)`**；新增：Database、Embedder、各 Store、IngestionWorker、DreamJob、MemoryRetriever 装配 |
| **D. `MateFairyRuntime`** | `runtime/MateFairyRuntime.kt` | 暴露 `ingestionWorker` / `dreamJob` / `embedder` / `memoryRetriever` 给生命周期使用 |
| **E. `AppConfigLoader`** | `config/AppConfig.kt` | 加 `parseMemoryConfig`；`AppConfig` 加 `memory` 字段；`MemoryConfig` 含 consolidate/factIngest/dream/ingestion/retrieval/embedding 6 子节点 |
| **F. `LaunchActivity`** | `platform/LaunchActivity.kt` | 覆写 `onPause`，仅调 `runtime.ingestionWorker.flush(timeoutPerJobMs=15_000)`；**不跑 Dream** |
| **G. `SpatialApplication`** | `platform/SpatialApplication.kt` | 持有 `lateinit var runtime`；`onCreate` 内同步装配 + 异步预热 Embedder |

---

## 二、模块清单 + 文件预算

按目标目录组织。**预计新增 22 个 Kotlin 文件**，约 **2000-2500 行代码**（不含模板/资源）。

```
app/src/main/java/com/example/matefairy01/
├── ml/
│   └── EmbedderOnnx.kt                         [~150 行] ONNX 加载 + tokenize + 推理 + mean pool
│
├── memory/
│   ├── ContextMemorySystem.kt                  [改造] 注入 L2/L3/L4
│   │
│   ├── MemoryConfig.kt                         [~60 行]  阈值配置
│   │
│   ├── db/
│   │   ├── MateFairyDatabase.kt                [~40 行]  @Database
│   │   ├── Converters.kt                       [~30 行]  ByteArray <-> FloatArray，Long 时间
│   │   └── DbProvider.kt                       [~30 行]  单例工厂
│   │
│   ├── episodic/
│   │   ├── EpisodicEntry.kt                    [~35 行]  @Entity
│   │   ├── EpisodicFts.kt                      [~15 行]  @Fts4 镜像
│   │   ├── EpisodicDao.kt                      [~80 行]  @Dao + FTS 查询
│   │   └── EpisodicStore.kt                    [~120 行] 高层 API: add / search / count / prune
│   │
│   ├── semantic/
│   │   ├── Fact.kt                             [~30 行]  @Entity
│   │   ├── Triple.kt                           [~25 行]  @Entity
│   │   ├── FactDao.kt                          [~80 行]
│   │   ├── TripleDao.kt                        [~50 行]
│   │   ├── SemanticStore.kt                    [~150 行] add / search / supersede / multiHop
│   │   └── ContradictionChecker.kt             [~120 行] LLM 矛盾检测 + entity token 过滤
│   │
│   ├── permanent/
│   │   ├── PermanentStore.kt                   [~150 行] 三个 md 读写 + 备份
│   │   ├── DreamJob.kt                         [~250 行] facts/episodic → md 蒸馏
│   │   └── MdTemplates.kt                      [~50 行]  初始模板
│   │
│   ├── ingestion/
│   │   ├── IngestionWorker.kt                  [~150 行] Channel 异步队列
│   │   ├── IngestionJob.kt                     [~30 行]  sealed class
│   │   ├── Consolidator.kt                     [~120 行] L1 → L2 摘要 + 向量化
│   │   ├── FactExtractor.kt                    [~180 行] LLM JSON 抽事实，含 importance 评分
│   │   ├── ShouldIngest.kt                     [~50 行]  启发式过滤
│   │   └── RecencyScorer.kt                    [~30 行]  半衰期衰减
│   │
│   └── retrieval/
│       └── MemoryRetriever.kt                  [~150 行] 三因子打分聚合 L2 + L3 + L4
│
└── platform/
    └── LaunchActivity.kt                       [改造] onPause 钩子

app/src/main/assets/
├── ml/
│   ├── bge-small-zh-v1.5-int8.onnx             [~30 MB] 模型
│   └── tokenizer.json                          [~6 MB]  HF tokenizer
└── memory/
    ├── SOUL.template.md                        [~50 行]  精灵人设模板
    ├── USER.template.md                        [~30 行]  用户画像模板
    └── MEMORY.template.md                      [~20 行]  叙事记忆模板

scripts/
└── export_bge.py                               [~80 行]  一次性：下载 bge-small-zh + ONNX 量化
```

---

## 三、阶段拆分（P1 内再分子阶段）

P1 目标 = 最小可用版上线。**子阶段顺序严格按依赖链**，每个子阶段独立可验证、独立可回滚。

### P1.0 基础设施（1-2 天）

**目标**：把基础依赖装上，跑通 hello world 级测试。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 0.1 改 Gradle，加 Room + KSP + lifecycle-process | `app/build.gradle.kts` `gradle/libs.versions.toml` `build.gradle.kts` | 依赖能 sync | `./gradlew build` 通过 |
| 0.2 写 `MemoryConfig.kt` + 扩展 `AppConfigLoader` | `app_config.json` 新增 `memory` 节点（embedding 含 baseUrl/apiKey/model/dimension） | 解析后 `AppConfig.memory` 可访问 | 单测：mock JSON → 解析正确 |
| 0.3 写 `IEmbedder.kt` 接口 + `EmbedderRemote.kt` 远端实现 | OkHttp + EmbeddingConfig | OpenAI 兼容 `/embeddings` HTTP 调用 | 单测：mock 服务 → 返回固定向量；连真机 → encode 真实 query 返回正确维度向量 |
| 0.4 维度断言 | runtime startup | 启动断言 `embedder.dimension == config.embedding.dimension` | 错配立即崩，不留隐患到 P1.1 |

**验收线**：能调用所选 embedding 服务返回正确维度的向量，配置可热插拔（改 baseUrl 即换 provider）。

> v3 删除项：~~ONNX 模型导出~~、~~`EmbedderOnnx.kt`~~、~~Tokenizer JNI 实测~~。这三项作为 P2 备选方案保留，P1 不做。

### P1.1 数据层（2 天）

**目标**：SQLite 跑通，三张表能读写。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 1.1 `MateFairyDatabase.kt` + `Converters.kt` + 三张 `@Entity` + 三个 `@Dao` | Room 注解 | 编译通过的 .db schema | KSP 生成 `MateFairyDatabase_Impl` |
| 1.2 `DbProvider` 单例工厂 | applicationContext | `provide(): MateFairyDatabase` | 应用启动后 .db 文件落地 |
| 1.3 `EpisodicStore` + `SemanticStore` 基础 CRUD | DAO | add/get/count 能用 | 单测：插 10 条查 10 条 |
| 1.4 Episodic FTS5 索引验证 | 中文摘要 | FTS MATCH 查询命中 | 单测：插 "用户喜欢咖啡" → 查 "咖啡" 命中 |

**验收线**：跑通 DAO 单元测试。Room 在 ARM64 PICO 上行为正常。

### P1.2 检索层（2 天）

**目标**：把向量召回拼起来，能查到相关记忆。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 2.1 `RecencyScorer` 半衰期衰减 | timestamp | `score(now, t): Float in [0,1]` | 单测：t=now → 1.0；t=now-7d → 0.5 |
| 2.2 `EpisodicStore.search(query, k)` 三因子打分 | embedder + dao + scorer | `List<EpisodicEntry>` | 集成测：插入 50 条不同语义 + 不同时间 → 查询排序合理 |
| 2.3 `SemanticStore.search` 类似 | 同上 | `List<Fact>` | 同 |
| 2.4 `MemoryRetriever.assembleContext(query)` | 三个 Store | 拼装好的文本块 | 集成测：返回结构化文本 |

**验收线**：mock 数据下，"咖啡" 查询能命中"用户喜欢美式咖啡" fact + 相关 episodic 摘要。

### P1.3 写入层（3 天）

**目标**：异步写入流水线跑通，对话不卡。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 3.1 `IngestionJob` sealed + `IngestionWorker` Channel | coroutines | enqueue/flush API | 单测：投 100 条任务全部消费 |
| 3.2 `Consolidator` 调 LLM summarize + 向量化 + 入 Episodic | LLM provider | 一次摘要入库 | 集成：mock LLM 返回固定字符串 → 表中多一行 |
| 3.3 `ShouldIngest` 启发式过滤 | messages | bool | 单测：闲聊跳过、长对话进入 |
| 3.4 `FactExtractor` LLM JSON 抽取 + importance 评分 | LLM provider | `List<Fact>` 入库 | 集成：固定 prompt → 抽出预期事实 |
| 3.5 `ContradictionChecker` 矛盾检测 + supersede | semantic store + LLM | 旧 fact 被标记 supersededBy | 集成：先插"喜欢咖啡"再插"不喜欢咖啡" → 旧 fact supersede |

**验收线**：跑一段假对话，能看到 episodic 表 + facts 表都有数据，主对话延迟无明显增加。

### P1.4 永久层（2 天）

**目标**：md 文件 + Dream 蒸馏跑通。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 4.1 `PermanentStore` 三 md 读写 + 备份 | filesDir | 自动从 assets 拷模板 | 首次启动后 filesDir/memory/ 三文件存在 |
| 4.2 `MdTemplates` 三份初始模板 | assets | 默认 SOUL/USER/MEMORY | — |
| 4.3 `DreamJob.run` 蒸馏：facts → USER.md, episodic → MEMORY.md | LLM + 三 store | md 文件被更新 | 集成：插假 facts → 跑 Dream → USER.md 多行 |
| 4.4 备份机制：每次 Dream 前 copy `*.md.bak` | 文件系统 | 一份备份 | 看文件是否生成 |

**验收线**：手动触发 Dream，能看到 md 文件被合理更新。

### P1.5 集成 + 触发点（2 天）

**目标**：所有触发点接入，端到端跑通。

| 步骤 | 输入 | 输出 | 验证 |
|---|---|---|---|
| 5.1 改造 `ContextMemorySystem.buildPromptMessages` | + L4 内容 + L2/L3 召回 | `build(query, retriever)` 注入 SOUL/USER/recall 到 prompt | 单测：返回 messages 含三段 system 注入 |
| 5.2 改造 `ConversationOrchestrator.processUserInput` | hook ingestionWorker | 回复后异步入队 | 端到端：发消息 → 对话回复 + 后台任务跑 |
| 5.3 改造 `MateFairyRuntimeFactory.create(context, config)` | 装配所有新模块 | 单一入口拿到完整 runtime（含 embedder / stores / ingestionWorker / dreamJob / memoryRetriever） | 启动不崩 |
| 5.3.1 ⚠️ 改造 `SpatialApplication` 持有 `runtime` | Application 单例 | `lateinit var runtime`，`onCreate` 装配 + IO scope 异步预热 Embedder | logcat 验证 `Embedder warmup done` 出现且不阻塞 UI |
| 5.4 ⚠️ 改造 `LaunchActivity.onPause` | 生命周期 | **仅** 调 `runtime.ingestionWorker.flush(timeoutPerJobMs=15_000)`；用 `applicationScope` 而非 `lifecycleScope` | logcat：摘头显时出现 `flush completed`；不要出现 Dream 相关日志 |
| 5.4.1 ⚠️ 验证 `SpatialLaunchActivity.onPause` 是否被 PICO SDK 拦截 | 实机 | onPause 日志能打出来 | 一行 Log 验证；99% 通过；若被吞，改用 `ProcessLifecycleOwner.get().lifecycle.addObserver` |
| 5.5 空闲计时器：`ConversationOrchestrator` 注册 `checkIdle` | 定时 | 5min 无互动触发 Dream（前台） | 实测：放置 5min → Dream 跑 |
| 5.6 调试用：手柄按键强制 Dream | InputControllerManager | 调试态可触发 | 验证 |

**验收线**：实机端到端测试：对话 5-10 轮 → 摘头显 → 重戴 → 精灵能主动提到之前聊过的事。

---

## 四、风险登记册

| 风险 | 等级 | 应对 |
|---|---|---|
| **远端 embedding 网络不稳** | 中 | OkHttp 设短 timeout（5s connect / 15s read）；失败返回零向量降级；P2 加常用 query 缓存 |
| **远端 embedding API key 失效 / 服务下线** | 中 | `IEmbedder` 接口抽象，可一键切 provider；监控 401/403/429 → 降级零向量 |
| **embedding 维度配置错位** | 高 | 启动断言 `embedder.dimension == config.embedding.dimension`；运行时校验向量长度 |
| **embedding 数据合规（对话内容上传第三方）** | 中 | 选用国内合规 provider（智谱 / 阿里 / 火山）；隐私声明告知用户 |
| **onPause 期跑 Dream 半路被杀** | 高 | onPause **只 flush 不 Dream**；Dream 仅前台 idle 跑 |
| **runtime 实例未在 Activity 可达** | 高 | `SpatialApplication.runtime` 升级为 `lateinit var` 单例；P1.5.3.1 单独验证 |
| **SpatialLaunchActivity 拦截 onPause** | 低 | P1.5.4.1 实测；失败用 `ProcessLifecycleOwner` 监听 |
| Room schema 变更后用户旧数据丢失 | 低（首版） | P1 不做 migration，破坏性更新；P2 引入版本号 |
| 每轮异步 LLM 调用累计成本 | 中 | `ShouldIngest` 严格过滤；`throttleMs` 节流；提供 mock provider 测试 |
| Dream 跑时正好被打断（用户重启 app） | 低 | 写 md 用临时文件 + rename 原子操作；备份机制兜底 |
| ContradictionChecker LLM 误判 | 中 | 矛盾检测仅做 `supersede` 软标记不删除；用户可在 USER.md 手动修复 |
| FTS5 中文分词效果差 | 中 | P1 主排序靠向量 cosine + recency + importance；FTS 仅做候选生成与排序加权（权重低） |
| 矩阵向量库本地存 BLOB 性能瓶颈 | 低 | N < 5000 暴力 cosine 仍然 < 100ms；超过再加 sqlite-vec |
| DeepSeek / mimo / embedding API 在 PICO 后台被掐 | 中 | onPause 仅 flush（短时网络）；Dream 仅前台 |
| API token 月度成本 | 低 | 陪伴量级低，月费几元到几十元 |

---

## 五、验证策略

### 5.1 单元测试（unit, 跑 JVM）

| 模块 | 测试点 |
|---|---|
| `MemoryConfig` JSON parser | 各字段默认值 / 异常字段容错 |
| `RecencyScorer` | 半衰期数学正确性 |
| `ShouldIngest` | 各启发式分支 |
| `EpisodicStore.search` 排序 | 三因子权重打分顺序正确 |
| `ContradictionChecker.entityTokenize` | 中文实体抽取 |
| `PermanentStore` md 读写 | 备份生成 / 模板拷贝 |

### 5.2 仪器测试（androidTest, 跑设备 / 模拟器）

| 模块 | 测试点 |
|---|---|
| `EmbedderOnnx` | 实机推理延迟 / 一致性 |
| Room Database 全 DAO | 增删查改 / FTS / Migration |
| `IngestionWorker` 端到端 | mock LLM → 对话 → 入库 |

### 5.3 端到端冒烟（手测）

清单：

1. 冷启动 → Embedder 预热完成 < 2s
2. 对话 "我对花生过敏" → 立即回复 → 后台 fact 表多一行
3. 连续对话 11 轮 → 自动触发 Consolidator → episodic 表多一行摘要
4. 摘头显 → onPause → 看 logcat 出现 `Dream completed`
5. 重戴 → 提问 "你记得我对什么过敏吗" → 精灵能正确回答（说明 L3/L4 注入 prompt 起效）
6. 反向陈述 "其实我不过敏花生了" → 后台触发 ContradictionChecker → 旧 fact `supersededBy` 被填充

---

## 六、不在 P1 范围内（明确 OUT）

**避免范围蔓延**，以下事项 P1 完成前不做：

- L3 triples 表的 LLM 抽取与多跳查询（schema 留位，runtime 不实现）
- JGit 版本化（用 `.bak` 简单备份替代）
- sqlite-vec ANN 索引（暴力 cosine 足够）
- WorkManager 周期任务（用 `onPause` + 空闲计时器）
- 多语言 embedding（中文优先）
- TTS 播报（已是另一条产品线）
- MCP memory tool（暴露给 LLM 自主调用，P3 才做）

---

## 七、配置文件示例

最终 `app_config.json` 长这样（追加 memory 节点）：

```json
{
  "ai": { "...": "..." },
  "mcpServers": { "...": "..." },
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

> `embedding.dimension` 必须与所选 model 实际维度严格一致：
> - 智谱 `embedding-3` = 1024
> - 阿里 `text-embedding-v3` = 1024
> - 火山 `doubao-embedding-text-240715` = 2048
> - OpenAI `text-embedding-3-small` = 1536
> - 百度 `embedding-v1` = 384

---

## 八、施工 checklist（粘贴到 PR 描述用）

P1 完成定义：

- [ ] P1.0 基础设施
  - [ ] Gradle 依赖 sync 通过（Room + KSP + lifecycle-process）
  - [ ] `MemoryConfig` + `AppConfigLoader` 支持 memory 节点（含 embedding 远端配置）
  - [ ] `IEmbedder` 接口 + `EmbedderRemote` 实现（OpenAI 兼容 `/embeddings`）
  - [ ] embedder 实测调用所选 provider，返回正确维度向量
  - [ ] 启动断言 `embedder.dimension == config.embedding.dimension`
- [ ] P1.1 数据层
  - [ ] Room 三张表编译通过
  - [ ] `EpisodicStore` / `SemanticStore` CRUD 单测通过
  - [ ] FTS5 中文 MATCH 验证（仅作候选生成，召回质量预期一般）
- [ ] P1.2 检索层
  - [ ] `RecencyScorer` 单测
  - [ ] `MemoryRetriever.assembleContext` 集成测
  - [ ] 主排序靠向量 cosine（FTS 权重低）
- [ ] P1.3 写入层
  - [ ] `IngestionWorker` 异步队列单测
  - [ ] `Consolidator` 集成测
  - [ ] `FactExtractor` 集成测
  - [ ] `ContradictionChecker` supersede 集成测
- [ ] P1.4 永久层
  - [ ] 三 md 模板拷贝
  - [ ] `DreamJob.run` 集成测
- [ ] P1.5 集成
  - [ ] **`SpatialApplication` 持有 `runtime` 单例**
  - [ ] `MateFairyRuntimeFactory.create(context, config)` 签名升级
  - [ ] `buildPromptMessages(query, retriever)` 注入 L2/L3/L4
  - [ ] `processUserInput` hook ingestion
  - [ ] **`LaunchActivity.onPause` 仅 flush，不跑 Dream**
  - [ ] **`SpatialLaunchActivity.onPause` 实机日志验证**
  - [ ] 空闲计时器（前台 5min 触发 Dream）
  - [ ] 端到端冒烟 6 项全过

---

## 九、施工顺序建议

**严格按 P1.0 → P1.5 顺序施工**。每个子阶段的输出是下一阶段的输入，并行只会制造合并地狱。

每个子阶段结束做一次提交（commit），便于回滚。建议 commit message 格式：

```
[memory][P1.X] 模块名：一句话改动
```

例：
- `[memory][P1.0] Embedder：接入 ONNX Runtime + bge-small-zh`
- `[memory][P1.3] FactExtractor：LLM JSON 抽取 + importance 评分`

---

## 十、参考资料

- 设计稿：`MEMORY_SYSTEM_DESIGN.md`
- NAVI 实现参考：`D:\xr\NAVI\backend\memory\episodic_memory.py` / `fact_memory.py`
- nanobot 实现参考：`D:\xr\nanobot\nanobot\agent\memory.py`
- 项目现状：`PROJECT_SUMMARY.md`
- ONNX Runtime Android：https://onnxruntime.ai/docs/get-started/with-android.html
- bge-small-zh-v1.5：https://huggingface.co/BAAI/bge-small-zh-v1.5
- Room 迁移指南：https://developer.android.com/training/data-storage/room/migrating-db-versions
