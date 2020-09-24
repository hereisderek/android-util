import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt


plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    // kotlin("de.mannodermaus.android-junit5")
}

inline fun <reified T> readGradleProperty(name: String, noinline lazyValue: (()->T?)? = null) : T? {
    val value = findProperty(name) ?: return lazyValue?.invoke()
    if (value is T) return value
    return when(T::class){
        Int::class -> when(value) {
            is Number -> value.toInt()
            is String -> value.toInt()
            else -> throw TypeCastException("unable to cast type:${T::class.simpleName} to Int, with value:$value")
        }
        else -> throw TypeCastException("unable to cast type:${T::class.simpleName} to Int, with value:$value")
    } as? T
}


android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(readGradleProperty<Int>("android.minSdkVersion"){16}!!)
        targetSdkVersion(readGradleProperty<Int>("android.targetSdkVersion"){29}!!)

        versionCode = readGradleProperty<Int>("android.versionCode")!!
        versionName = readGradleProperty("android.versionName")!!

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        animationsDisabled = true
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Optimize APK size - remove excess files in the manifest and APK
    packagingOptions {
        exclude("**/*.kotlin_module")
        exclude("**/*.version")
        exclude("**/kotlin/**")
        exclude("**/*.txt")
        exclude("**/*.xml")
        exclude("**/*.properties")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    androidExtensions {
        isExperimental = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation (kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    // implementation (Libs.kotlin_stdlib)
    implementation (Libs.timber)
    implementation (Libs.appcompat)
    implementation (Libs.core_ktx)
    implementation (Libs.viewpager2)
    implementation (Libs.exifinterface)
    implementation (Libs.lifecycle_viewmodel_ktx)

    implementation (Libs.kotlinx_coroutines_core)
    implementation (Libs.kotlinx_coroutines_android)
    implementation (Libs.kotlinx_serialization_runtime)
    implementation (Libs.kotlin_android_extensions_runtime)

    testImplementation (Libs.junit_jupiter_api)
    testImplementation (Libs.junit_jupiter_params)
    testRuntimeOnly (Libs.junit_jupiter_engine)
    // testImplementation (Libs.junit_junit)
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

// apply(from = "https://raw.githubusercontent.com/sky-uk/gradle-maven-plugin/master/gradle-mavenizer.gradle")
apply(from = "https://raw.githubusercontent.com/jokermonn/gradle-maven-plugin/master/gradle-mavenizer.gradle")

