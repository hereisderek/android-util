rootProject.name = "android-util"

include(
    ":app", ":base"
)

// project(":util").projectDir = File(settingsDir, "util/base")


// ./settings.gradle.kts
pluginManagement {
    repositories {
    }

    val resolutionStrategyConfig: String? by extra
    resolutionStrategy.eachPlugin {
        val property = "plugin.${requested.id.id}"
        if (extra.has(property) && resolutionStrategyConfig != "false") {
            val version = extra.get(property) as String
            if (resolutionStrategyConfig == "verbose") println("ResolutionStrategy selected version=$version from property=$property")
            useVersion(version)
        }
    }
}