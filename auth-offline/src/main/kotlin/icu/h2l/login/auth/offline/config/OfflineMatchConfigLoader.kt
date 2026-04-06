package icu.h2l.login.auth.offline.config

import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.objectmapping.ObjectMapper
import java.nio.file.Files
import java.nio.file.Path

object OfflineMatchConfigLoader {
	private lateinit var config: OfflineMatchConfig

	fun load(dataDirectory: Path) {
		val path = dataDirectory.resolve("offlinematch.conf")
		val firstCreation = Files.notExists(path)
		val loader = HoconConfigurationLoader.builder()
			.defaultOptions { opts: ConfigurationOptions ->
				opts
					.shouldCopyDefaults(true)
					.header(
						"""
							HyperZoneLogin Offline Match Configuration
						""".trimIndent()
					)
					.serializers { s ->
						s.registerAnnotatedObjects(
							ObjectMapper.factoryBuilder().addDiscoverer(dataClassFieldDiscoverer()).build()
						)
					}
			}
			.path(path)
			.build()

		val node = loader.load()
		val cfg = node.get(OfflineMatchConfig::class.java) ?: OfflineMatchConfig()

		if (firstCreation) {
			node.set(cfg)
			loader.save(node)
		}

		config = cfg
	}

	fun getConfig(): OfflineMatchConfig = config
}


