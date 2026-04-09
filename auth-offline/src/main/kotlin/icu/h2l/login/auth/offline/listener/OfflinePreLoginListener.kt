package icu.h2l.login.auth.offline.listener

import com.velocitypowered.api.event.Subscribe
import icu.h2l.api.event.connection.OpenPreLoginEvent
// HyperZonePlayerManager.create(...) belongs to core pre-login initialization and is intentionally
// kept in the core `velocity` EventListener. Do not initialize channel here to avoid ordering issues.
import icu.h2l.login.auth.offline.util.ExtraUuidUtils
import icu.h2l.login.auth.offline.type.OfflineUUIDType
import icu.h2l.login.auth.offline.config.OfflineMatchConfigLoader
import icu.h2l.api.log.info

class OfflinePreLoginListener {
    @Subscribe
    fun onPreLogin(event: OpenPreLoginEvent) {
        val uuid = event.uuid
        val name = event.userName
        val host = event.host
        // channel/player initialization is performed by the main plugin's EventListener

        val cfg = OfflineMatchConfigLoader.getConfig()
        if (!cfg.enable) return

        val offlineHost = cfg.hostMatch.start.any { it.startsWith(host) }
        if (offlineHost) {
            info { "匹配到离线 host 玩家: $name" }
        }
        val offlineUUIDType = ExtraUuidUtils.matchType(uuid, name)

        if (offlineUUIDType != OfflineUUIDType.UNKNOWN || offlineHost) {
            event.isOnline = false
        } else {
            event.isOnline = true
        }
        info { "传入 UUID 信息玩家: $name UUID:$uuid 类型: $offlineUUIDType 在线:${event.isOnline}" }
    }
}


