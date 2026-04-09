package icu.h2l.login.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class BackendServerConfig {
    @Comment("当未安装 limboapi 时，使用真实后端服务器作为认证等待区；留空表示禁用")
    val fallbackAuthServer: String = "lobby"

    @Comment("登入完成后优先进入的子服务器；若为空或找不到该服务器，则继续按其他候选顺序选择")
    val postAuthDefaultServer: String = "play"

    @Comment("在真实服务器认证等待区内，如果玩家尝试前往其他服务器，是否记住新的目标并在认证成功后自动连接")
    val rememberRequestedServerDuringAuth: Boolean = true
}

