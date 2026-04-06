package icu.h2l.login.auth.offline

import com.velocitypowered.api.proxy.ProxyServer
import icu.h2l.api.command.HyperChatCommandManagerProvider
import icu.h2l.api.db.HyperZoneDatabaseManager
import icu.h2l.api.db.table.ProfileTable
import icu.h2l.api.log.info
import icu.h2l.api.module.HyperSubModule
import icu.h2l.api.player.HyperZonePlayerAccessorProvider
import icu.h2l.login.auth.offline.command.OfflineAuthCommandRegistrar
import icu.h2l.login.auth.offline.db.OfflineAuthRepository
import icu.h2l.login.auth.offline.db.OfflineAuthTableManager
import icu.h2l.login.auth.offline.service.OfflineAuthService
import icu.h2l.login.auth.offline.config.OfflineMatchConfigLoader
import icu.h2l.login.auth.offline.listener.OfflinePreLoginListener
import java.nio.file.Path

class OfflineSubModule : HyperSubModule {
    lateinit var offlineAuthTableManager: OfflineAuthTableManager
    lateinit var offlineAuthRepository: OfflineAuthRepository
    lateinit var offlineAuthService: OfflineAuthService

    override fun register(
        owner: Any,
        proxy: ProxyServer,
        dataDirectory: Path,
        databaseManager: HyperZoneDatabaseManager
    ) {
        val commandManagerProvider = owner as? HyperChatCommandManagerProvider
            ?: throw IllegalStateException("OfflineSubModule requires HyperChatCommandManagerProvider owner")
        val playerAccessorProvider = owner as? HyperZonePlayerAccessorProvider
            ?: throw IllegalStateException("OfflineSubModule requires HyperZonePlayerAccessorProvider owner")

        val profileTable = ProfileTable(databaseManager.tablePrefix)
        // Load offline matching configuration for this module
        OfflineMatchConfigLoader.load(dataDirectory)
        offlineAuthTableManager = OfflineAuthTableManager(
            databaseManager = databaseManager,
            tablePrefix = databaseManager.tablePrefix,
            profileTable = profileTable
        )
        offlineAuthRepository = OfflineAuthRepository(
            databaseManager = databaseManager,
            table = offlineAuthTableManager.offlineAuthTable
        )
        offlineAuthService = OfflineAuthService(
            repository = offlineAuthRepository,
            playerAccessor = playerAccessorProvider.hyperZonePlayers
        )
        offlineAuthTableManager.createTable()
        proxy.eventManager.register(owner, offlineAuthTableManager)

        // Register pre-login listener (handles channel init + offline UUID matching)
        proxy.eventManager.register(owner, OfflinePreLoginListener())

        OfflineAuthCommandRegistrar.registerAll(
            commandManager = commandManagerProvider.chatCommandManager,
            authService = offlineAuthService
        )
        proxy.eventManager.register(owner, OfflineLimboEventListener())
        info { "OfflineSubModule 已加载，离线聊天命令与提示监听器已注册" }
    }
}