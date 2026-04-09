import org.gradle.api.tasks.Sync
import org.gradle.jvm.tasks.Jar

buildscript {
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://plugins.gradle.org/m2/")
            }
            filter {
                includeGroup("com.github.johnrengelman")
                includeGroup("com.github.johnrengelman.shadow")
            }
        }

        maven("https://maven.aliyun.com/repository/central")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
        classpath("com.github.johnrengelman:shadow:8.1.1")
    }
}

plugins {
    base
}

subprojects {
    group = "icu.h2l.login"
    version = "1.0-SNAPSHOT"

    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.elytrium.net/repo/")
    }
}

val pluginBundleDir = layout.buildDirectory.dir("HZL")

val collectPluginJars by tasks.registering(Sync::class) {
    group = "build"
    description = "Collects all non-API plugin jars into one directory. velocity uses shadowJar; all other modules are prefixed with HZL-."
    into(pluginBundleDir)

    val velocityProject = project(":velocity")
    dependsOn(velocityProject.tasks.named("shadowJar"))
    from(velocityProject.tasks.named("shadowJar", Jar::class).flatMap { it.archiveFile })

    subprojects
        .filter { it.path != ":api" && it.path != ":velocity" }
        .forEach { subproject ->
            dependsOn(subproject.tasks.named("jar"))
            from(subproject.tasks.named("jar", Jar::class).flatMap { it.archiveFile }) {
                rename { fileName ->
                    if (fileName.startsWith("HZL-")) fileName else "HZL-$fileName"
                }
            }
        }
}

tasks.named("assemble") {
    dependsOn(collectPluginJars)
}

tasks.named("build") {
    dependsOn(collectPluginJars)
}
