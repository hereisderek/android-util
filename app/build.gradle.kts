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
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    // implementation (kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    // implementation (Kotlin.stdlibJdk7)
    implementation (Libs.kotlin_stdlib)
    implementation (Libs.timber)
    implementation (Libs.appcompat)
    implementation (Libs.core_ktx)

    testImplementation (Libs.junit_junit)
    androidTestImplementation (Libs.androidx_test_ext_junit)
    androidTestImplementation (Libs.espresso_core)


    /*
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.3.50")
    implementation ("com.jakewharton.timber:timber:4.7.1")
    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("androidx.core:core-ktx:1.1.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0-rc01")
    implementation ("androidx.exifinterface:exifinterface:1.1.0-rc01")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0-1.3.60-eap-76")

    testImplementation ("junit:junit:4.12")
    androidTestImplementation ("androidx.test.ext:junit:1.1.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.2.0")
    */

}