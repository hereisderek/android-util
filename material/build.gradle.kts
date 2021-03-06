
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

@Suppress("unchecked_cast", "nothing_to_inline")
inline fun <T> uncheckedCast(target: Any?): T = target as T


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
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(readGradleProperty<Int>("android.minSdkVersion"){22}!!)
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

}




dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation (Libs.kotlin_stdlib)
    implementation (Libs.timber)
    implementation (Libs.appcompat)
    implementation (Libs.core_ktx)
    implementation ("com.google.android.material:material:1.3.0-alpha02")
    implementation (project(":base"))

    testImplementation ("junit:junit:4.13")
    androidTestImplementation ("androidx.test.ext:junit:1.1.1")
    androidTestImplementation ("androidx.test.ext:junit-ktx:1.1.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.2.0")
}