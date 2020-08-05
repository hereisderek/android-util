import de.fayard.OrderBy
import de.fayard.internal.PluginConfig.isNonStable
import org.jetbrains.kotlin.config.KotlinCompilerVersion


plugins {
    id("de.fayard.buildSrcVersions") version "0.7.0"
    id("de.fayard.dependencies") version "0.5.6"
    kotlin("plugin.serialization") version "1.3.72"
}


buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0-alpha07")
        classpath(Libs.kotlin_serialization)
        classpath(Libs.kotlin_gradle_plugin)
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

/*tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}*/


buildSrcVersions {
    // See configuration options at https://github.com/jmfayard/buildSrcVersions/issues/53
    orderBy = OrderBy.GROUP_AND_ALPHABETICAL
    // propertiesFile = "versions.properties"
    //alignVersionsForGroups = listOf()
    renameLibs = "Libs"
    renameVersions = "Versions"
}