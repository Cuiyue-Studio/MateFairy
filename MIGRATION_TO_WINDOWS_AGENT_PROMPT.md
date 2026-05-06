# 项目跨平台迁移清理任务 (macOS -> Windows)

## 给 Trae Agent 的执行指令

你好 Agent，我刚把这个项目从 macOS 迁移到了 Windows。请你帮我执行以下环境清理任务，以避免本地路径冲突和缓存报错。请调用你的文件管理工具（如 `DeleteFile`）或终端工具（如 `RunCommand`）自动完成以下步骤：

### 1. 删除包含 macOS 绝对路径的本地配置文件
- 删除项目根目录下的 `local.properties` 文件。（由于里面硬编码了 Mac 的 SDK 路径，删除后 Android Studio 会在 Windows 环境下自动重新生成）。

### 2. 清理 macOS 环境下的构建与缓存目录
请查找并删除以下文件夹及其内部所有内容（如果存在的话）：
- `/.idea/` (Android Studio 的本地工作区配置)
- `/.gradle/` (Gradle 的本地缓存)
- `/build/` (项目级构建产物)
- `/app/build/` (App 模块构建产物)
- `/editor-asset/build/` (如果存在，清除 Editor 资产的构建产物)

### 3. 清除 macOS 特有的系统隐藏文件
- 遍历项目目录，删除所有的 `.DS_Store` 文件。
  - *如果是使用 Windows 终端工具，可以执行：`del /s /q /a:h .DS_Store`*

### 4. 任务完成报告
- 确认上述文件和目录清理完毕后，请告诉我清理结果。
- 提醒我：“清理已完成！现在可以点击 Android Studio 右上角的大象图标（Sync Project with Gradle Files）来重新同步 Windows 环境的 SDK 和依赖了。”