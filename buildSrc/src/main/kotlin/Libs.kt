import kotlin.String

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Update this file with
 *   `$ ./gradlew buildSrcVersions`
 */
object Libs {
    const val databinding_adapters: String = "androidx.databinding:databinding-adapters:" +
            Versions.androidx_databinding

    /**
     * https://developer.android.com/studio
     */
    const val databinding_common: String = "androidx.databinding:databinding-common:" +
            Versions.androidx_databinding

    /**
     * https://developer.android.com/studio
     */
    const val databinding_compiler: String = "androidx.databinding:databinding-compiler:" +
            Versions.androidx_databinding

    const val databinding_runtime: String = "androidx.databinding:databinding-runtime:" +
            Versions.androidx_databinding

    const val viewbinding: String = "androidx.databinding:viewbinding:" +
            Versions.androidx_databinding

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_ext_junit: String = "androidx.test.ext:junit:" +
            Versions.androidx_test_ext

    /**
     * https://developer.android.com/testing
     */
    const val junit_ktx: String = "androidx.test.ext:junit-ktx:" + Versions.androidx_test_ext

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions: String =
            "org.jetbrains.kotlin:kotlin-android-extensions:" + Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions_runtime: String =
            "org.jetbrains.kotlin:kotlin-android-extensions-runtime:" +
            Versions.org_jetbrains_kotlin

    const val kotlin_gradle_plugin: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:" +
            Versions.org_jetbrains_kotlin

    const val kotlin_serialization: String = "org.jetbrains.kotlin:kotlin-serialization:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib: String = "org.jetbrains.kotlin:kotlin-stdlib:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib_jdk8: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://github.com/Kotlin/kotlinx.coroutines
     */
    const val kotlinx_coroutines_android: String =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:" +
            Versions.org_jetbrains_kotlinx_kotlinx_coroutines

    /**
     * https://github.com/Kotlin/kotlinx.coroutines
     */
    const val kotlinx_coroutines_core: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" +
            Versions.org_jetbrains_kotlinx_kotlinx_coroutines

    /**
     * https://github.com/Kotlin/kotlinx.serialization
     */
    const val kotlinx_serialization_runtime: String =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime:" +
            Versions.org_jetbrains_kotlinx_kotlinx_serialization

    const val com_android_tools_build_gradle: String = "com.android.tools.build:gradle:" +
            Versions.com_android_tools_build_gradle

    /**
     * http://junit.org
     */
    const val junit_junit: String = "junit:junit:" + Versions.junit_junit

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val appcompat: String = "androidx.appcompat:appcompat:" + Versions.appcompat

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val core_ktx: String = "androidx.core:core-ktx:" + Versions.core_ktx

    const val de_fayard_buildsrcversions_gradle_plugin: String =
            "de.fayard.buildSrcVersions:de.fayard.buildSrcVersions.gradle.plugin:" +
            Versions.de_fayard_buildsrcversions_gradle_plugin

    const val de_fayard_dependencies_gradle_plugin: String =
            "de.fayard.dependencies:de.fayard.dependencies.gradle.plugin:" +
            Versions.de_fayard_dependencies_gradle_plugin

    /**
     * https://developer.android.com/testing
     */
    const val espresso_core: String = "androidx.test.espresso:espresso-core:" +
            Versions.espresso_core

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val exifinterface: String = "androidx.exifinterface:exifinterface:" +
            Versions.exifinterface

    /**
     * https://developer.android.com/studio
     */
    const val lint_gradle: String = "com.android.tools.lint:lint-gradle:" + Versions.lint_gradle

    /**
     * https://github.com/material-components/material-components-android
     */
    const val material: String = "com.google.android.material:material:" + Versions.material

    const val org_jetbrains_kotlin_plugin_serialization_gradle_plugin: String =
            "org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:" +
            Versions.org_jetbrains_kotlin_plugin_serialization_gradle_plugin

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val recyclerview: String = "androidx.recyclerview:recyclerview:" + Versions.recyclerview

    /**
     * https://github.com/JakeWharton/timber
     */
    const val timber: String = "com.jakewharton.timber:timber:" + Versions.timber

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val viewpager2: String = "androidx.viewpager2:viewpager2:" + Versions.viewpager2
}
