import de.fayard.OrderBy
import de.fayard.internal.PluginConfig.isNonStable
import org.jetbrains.kotlin.config.KotlinCompilerVersion


plugins {
    id("de.fayard.refreshVersions") version Versions.de_fayard_refreshversions_gradle_plugin
    // id("de.fayard.refreshVersions") version "0.8.4"
    // `build-scan` version "3.0"
    // id("se.patrikerdes.use-latest-versions") version Versions.se_patrikerdes_use_latest_versions_gradle_plugin
    // id("com.github.ben-manes.versions") version Versions.com_github_ben_manes_versions_gradle_plugin
    // id("org.jetbrains.gradle.plugin.idea-ext") version "0.4.2"
    // kotlin("multiplatform") version "1.3.50"
    kotlin("plugin.serialization") version Versions.kotlin_serialization
    // kotlin("jvm")
}


buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Libs.kotlin_serialization)
        classpath(Libs.kotlin_gradle_plugin)
        // classpath(Libs.com_android_tools_build_gradle)
        classpath ("com.android.tools.build:gradle:4.0.0-alpha09")
        // classpath(kotlin("gradle-plugin", version = "1.3.60"))
        // kotlinCompilerClasspath(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
        // kotlinCompilerPluginClasspath(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    }

    repositories {
        google()
        jcenter()
        mavenCentral()
        maven (url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://jitpack.io")
    }
}

subprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        mavenLocal()
        maven ( url = "https://jitpack.io" )
        maven ( url = "https://dl.bintray.com/kotlin/kotlin-eap" )
    }
}

/*
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
*/


buildSrcVersions {
    // See configuration options at https://github.com/jmfayard/buildSrcVersions/issues/53
    orderBy = OrderBy.GROUP_AND_ALPHABETICAL
    // propertiesFile = "versions.properties"
    //alignVersionsForGroups = listOf()
    renameLibs = "Libs"
    renameVersions = "Versions"
}

/**
 * Use ./gradlew refreshVersions to find available updates
 * See https://github.com/jmfayard/buildSrcVersions/issues/77
 *
 * Use ./gradlew buildSrcVersions to generate buildSrc/src/main/Libs.kt
 * See https://github.com/jmfayard/buildSrcVersions/issues/88
 */
/*
buildSrcVersions {
    // See configuration options at https://github.com/jmfayard/buildSrcVersions/issues/53

    rejectVersionIf {
        isNonStable(candidate.version)
    }
    //alwaysUpdateVersions()
    orderBy = OrderBy.GROUP_AND_LENGTH
    versionsOnlyMode = null
    versionsOnlyFile = null
    indent = null
    renameLibs = "Libs"
    renameVersions = "Versions"
    useFqdnFor() // nothing

}*/
