Auth Floodgate 模块 (`hzl-auth-floodgate`)
=========================================

用途
----
- 识别由 Floodgate 接入的 Bedrock 玩家。
- 在主插件的初始 `GameProfile` 校验阶段，通过 Floodgate API 对匹配玩家放行，避免被核心 remap 前缀校验误拦截。

运行时行为
----------
- 依赖主插件 `hyperzonelogin` 提供 API。
- 仅在代理已安装 `floodgate` 插件时注册模块；若未安装，则自动跳过，不启用任何相关监听。
- 单文件版中也支持内置加载：若 `modules.conf` 中 `authFloodgate=true` 且检测到 `floodgate`，则会自动启用内置版本。

