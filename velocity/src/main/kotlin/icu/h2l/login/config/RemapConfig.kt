package icu.h2l.login.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class RemapConfig {
    @Comment("生成UUID时的前缀，如果是OfflinePlayer则为离线生成方式")
    val prefix = "HyperZone"
}
