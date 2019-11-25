import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt


plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}


// apply(from = "android.gradle")
android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(29)
        targetSdkVersion(29)
        versionCode = 3
        versionName = "0.2.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    // implementation (kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    // implementation (Kotlin.stdlibJdk7)
    implementation (Libs.kotlin_stdlib)
    implementation (Libs.timber)
    implementation (Libs.appcompat)
    implementation (Libs.core_ktx)
    implementation (Libs.viewpager2)
    implementation (Libs.exifinterface)
    implementation (Libs.kotlinx_coroutines_core)
    implementation (Libs.kotlinx_coroutines_android)
    implementation (Libs.kotlin_android_extensions_runtime)
    implementation (Libs.kotlinx_serialization_runtime)

    testImplementation (Libs.junit_junit)
    androidTestImplementation (Libs.androidx_test_ext_junit)
    androidTestImplementation (Libs.espresso_core)
}

project.ext.apply {
    val mavSiteUrl = "https://github.com/hereisderek/android-util"
    set("mavSiteUrl", mavSiteUrl)
    set("mavGitUrl", "$mavSiteUrl.git")
    set("mavProjectName", "android-util")
    set("mavPublishToMavenLocal", true)
    set("mavLibraryLicenses", mapOf("Apache-2.0" to "http://www.apache.org/licenses/LICENSE-2.0.txt"))
    set("mavLibraryDescription", "A collection of android utilities")

}


version = android.defaultConfig.versionName as String

apply(from = "https://raw.githubusercontent.com/sky-uk/gradle-maven-plugin/master/gradle-mavenizer.gradle")

