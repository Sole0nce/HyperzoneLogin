pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven("https://plugins.gradle.org/m2/")
        gradlePluginPortal()
        mavenCentral()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> {
                    useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                }

                "com.github.johnrengelman.shadow" -> {
                    useModule("com.github.johnrengelman:shadow:${requested.version}")
                }
            }
        }
    }
}

rootProject.name = "HyperzoneLogin"

include("velocity")
include("api")
include("auth-yggd")
include("auth-offline")
include("data-merge")
include("profile-skin")
