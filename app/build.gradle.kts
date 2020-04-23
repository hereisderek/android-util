import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.config.KotlinCompilerVersion


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}


// apply(from = "android.gradle")
android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.github.hereisderek.androidutil.app"
        minSdkVersion(23)
        targetSdkVersion(29)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation (Libs.kotlin_stdlib)
    implementation (Libs.timber)
    implementation (Libs.appcompat)
    implementation (Libs.core_ktx)
    implementation (Libs.viewpager2)
    implementation (Libs.exifinterface)
    implementation (Libs.kotlinx_coroutines_core)
    implementation (Libs.kotlinx_coroutines_android)
    implementation (Libs.kotlinx_serialization_runtime)

    testImplementation ("junit:junit:4.13")
    androidTestImplementation ("androidx.test.ext:junit:1.1.1")
    androidTestImplementation ("androidx.test.ext:junit-ktx:1.1.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.2.0")
}