# Limbo 命令系统架构说明

## 系统概述

Limbo 命令系统是一个为 HyperzoneLogin 项目设计的轻量级命令框架，专门用于处理玩家在 Limbo 状态下的交互�?

## 设计特点

### 1. 无前缀命令
- 玩家可以直接输入命令名称，无需添加 `/` 等前缀
- 支持可选的命令前缀配置
- 支持严格模式（强制使用前缀�?

### 2. 灵活的命令注�?
提供三种命令注册方式�?
- **接口实现**: 实现 `LimboCommand` 接口
- **DSL 构建�?*: 使用 Kotlin DSL 快速创建命�?
- **Brigadier 适配**: 与外部命令系统集�?

### 3. 可扩展架�?
- 命令监听器机制，支持外部系统监听命令注册事件
- 自定义命令处理器，每个会话可以有独立的命令集
- Brigadier 适配器，方便与其他系统集�?

## 目录结构

```
velocity/src/main/kotlin/icu/h2l/login/limbo/
├── command/
�?  ├── LimboCommand.kt                    # 命令接口
�?  ├── LimboCommandHandler.kt             # 命令处理�?
�?  ├── LimboCommandManager.kt             # 全局命令管理�?
�?  ├── LimboCommandInitializer.kt         # 命令初始化器
�?  ├── bridge/
�?  �?  └── BrigadierAdapter.kt             # Brigadier 适配�?
�?  └── commands/
�?      ├── HelpCommand.kt                 # 帮助命令
�?      ├── LoginCommand.kt                # 登录命令
�?      ├── ExitCommand.kt                 # 退出命�?
�?      └── InfoCommand.kt                 # 信息命令
└── handler/
    └── LimboAuthSessionHandler.kt         # 会话处理器（已集成命令系统）
```

## 核心类说�?

### LimboCommand (接口)
```kotlin
interface LimboCommand {
    val name: String                       // 命令名称
    val aliases: List<String>              // 命令别名
    val description: String                // 命令描述
    val usage: String                      // 使用说明
    
    fun execute(...)                       // 执行命令
    fun hasPermission(...)                 // 权限检�?
    fun onTabComplete(...)                 // Tab 补全
}
```

### LimboCommandHandler (�?
负责命令的解析和执行�?
- 维护命令注册�?
- 解析玩家输入
- 分发命令到对应的处理�?
- 处理未知命令

### LimboCommandManager (单例)
全局命令管理�?
- 提供统一的命令注册入�?
- 维护全局命令处理�?
- 管理命令监听�?
- 提供 DSL 构建�?

### LimboAuthSessionHandler (�?
已集成命令系统：
- `onChat` 方法自动调用命令处理�?
- 支持设置自定义命令处理器
- 默认使用全局命令管理�?

## 工作流程

```
玩家输入消息
    �?
LimboAuthSessionHandler.onChat()
    �?
LimboCommandHandler.handleMessage()
    �?
解析命令和参�?
    �?
查找命令实例
    �?
检查权�?
    �?
执行命令
    �?
返回结果
```

## 集成方式

### 1. 标准集成（已完成�?

�?`HyperZoneLoginMain` 中：

```kotlin
private fun initializeLimboCommands() {
    // 注册内置命令
    LimboCommandInitializer.registerDefaultCommands()
}
```

### 2. Brigadier 集成

启用 Brigadier 适配器：

```kotlin
BrigadierAdapter.enableBrigadier()
```

创建并注�?Brigadier 命令�?

```kotlin
val handler = object : SimpleBrigadierCommandHandler("mycommand", "描述") {
    override fun execute(...): Boolean {
        // 命令逻辑
        return true
    }
}

val command = BrigadierAdapter.createBrigadierCommand("mycommand", handler)
LimboCommandManager.registerCommand(command)
```

### 3. 监听器集�?

监听命令注册事件�?

```kotlin
LimboCommandManager.addRegistrationListener(object : CommandRegistrationListener {
    override fun onCommandRegistered(command: LimboCommand) {
        // 处理命令注册事件
    }
    
    override fun onCommandUnregistered(commandName: String) {
        // 处理命令取消注册事件
    }
})
```

## 使用示例

### 示例 1: 创建简单命�?

```kotlin
class PingCommand : LimboCommand {
    override val name = "ping"
    
    override fun execute(handler: LimboAuthSessionHandler, player: Player, args: Array<String>): Boolean {
        handler.sendMessage(Component.text("Pong!"))
        return true
    }
}

LimboCommandManager.registerCommand(PingCommand())
```

### 示例 2: 使用 DSL

```kotlin
LimboCommandManager.command("echo") {
    description("回显消息")
    usage("echo <消息>")
    
    execute { handler, player, args ->
        if (args.isEmpty()) return@execute false
        handler.sendMessage(Component.text(args.joinToString(" ")))
        true
    }
}
```

### 示例 3: 带权限的命令

```kotlin
LimboCommandManager.command("admin") {
    description("管理命令")
    permission("hyperzonelogin.admin")
    
    execute { handler, player, args ->
        handler.sendMessage(Component.text("管理功能"))
        true
    }
}
```

## 扩展建议

### 1. 添加命令冷却系统
```kotlin
class CooldownManager {
    private val cooldowns = ConcurrentHashMap<String, Long>()
    
    fun setCooldown(player: String, command: String, duration: Long) {
        cooldowns["$player:$command"] = System.currentTimeMillis() + duration
    }
    
    fun isOnCooldown(player: String, command: String): Boolean {
        val key = "$player:$command"
        val expireTime = cooldowns[key] ?: return false
        if (System.currentTimeMillis() >= expireTime) {
            cooldowns.remove(key)
            return false
        }
        return true
    }
}
```

### 2. 添加命令别名映射
```kotlin
class CommandAliasMapper {
    private val aliases = mutableMapOf<String, String>()
    
    fun addAlias(alias: String, command: String) {
        aliases[alias.lowercase()] = command.lowercase()
    }
    
    fun resolve(input: String): String {
        return aliases[input.lowercase()] ?: input
    }
}
```

### 3. 添加命令历史记录
```kotlin
class CommandHistory {
    private val history = ConcurrentHashMap<String, MutableList<String>>()
    
    fun addCommand(player: String, command: String) {
        history.computeIfAbsent(player) { mutableListOf() }.add(command)
    }
    
    fun getHistory(player: String): List<String> {
        return history[player]?.toList() ?: emptyList()
    }
}
```

## 性能优化

1. **命令缓存**: 命令实例在注册时缓存，避免重复创�?
2. **并发安全**: 使用 `ConcurrentHashMap` 确保线程安全
3. **懒加�?*: 全局命令处理器采用单例模�?
4. **智能解析**: 参数解析优化，支持引号和转义字符

## 安全考虑

1. **权限检�?*: 每个命令都可以实现自定义权限检�?
2. **参数验证**: 建议�?execute 方法中验证参�?
3. **异常处理**: 命令执行异常会被捕获并显示给玩家
4. **注入防护**: 参数解析防止命令注入攻击

## 未来计划

- [ ] 支持异步命令执行
- [ ] 添加命令使用统计
- [ ] 支持命令国际�?
- [ ] 实现命令权重/优先级系�?
- [ ] 添加命令帮助文档生成�?
- [ ] 支持子命令系�?
- [ ] 实现命令参数类型验证

## 相关文档

- [使用文档](LIMBO_COMMAND_USAGE.md) - 详细的使用指�?
- [API 文档](docs/api/) - 完整�?API 参�?
- [示例代码](examples/) - 更多示例代码

## 贡献指南

欢迎贡献新的命令或改进现有系统！请确保：

1. 遵循项目代码风格
2. 添加适当的注�?
3. 编写单元测试
4. 更新相关文档

## 许可�?

遵循项目主许可证�?

