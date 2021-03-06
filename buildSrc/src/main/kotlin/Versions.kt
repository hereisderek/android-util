import kotlin.String
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version.
 */
object Versions {
    const val androidx_databinding: String = "4.2.0-alpha07"

    const val androidx_test_ext: String = "1.1.1"

    const val org_jetbrains_kotlin: String = "1.3.72"

    const val org_jetbrains_kotlinx_kotlinx_coroutines: String = "1.3.8"

    //1.0-M1-1.4.0-rc
    const val org_jetbrains_kotlinx_kotlinx_serialization: String = "1.0-M1-1.4.0-rc"//"0.14.0-1.3.60-eap-76"

    const val com_android_tools_build_gradle: String = "4.2.0-alpha07"

    const val junit_junit: String = "4.13"

    const val appcompat: String = "1.2.0"

    const val core_ktx: String = "1.3.1"

    const val de_fayard_buildsrcversions_gradle_plugin: String = "0.7.0"

    const val de_fayard_dependencies_gradle_plugin: String = "0.5.6" // available: "0.5.8"

    const val espresso_core: String = "3.2.0"

    const val exifinterface: String = "1.2.0"

    const val lint_gradle: String = "27.2.0-alpha07"

    const val material: String = "1.3.0-alpha02"

    const val org_jetbrains_kotlin_plugin_serialization_gradle_plugin: String = "1.3.72"

    const val recyclerview: String = "1.1.0"

    const val timber: String = "4.7.1"

    const val viewpager2: String = "1.0.0"

    /**
     * Current version: "6.5.1"
     * See issue 19: How to update Gradle itself?
     * https://github.com/jmfayard/buildSrcVersions/issues/19
     */
    const val gradleLatestVersion: String = "6.5.1"
}

/**
 * See issue #47: how to update buildSrcVersions itself
 * https://github.com/jmfayard/buildSrcVersions/issues/47
 */
val PluginDependenciesSpec.buildSrcVersions: PluginDependencySpec
    inline get() =
            id("de.fayard.buildSrcVersions").version(Versions.de_fayard_buildsrcversions_gradle_plugin)
