plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rtmart.rtretrofitlib"
    compileSdk = 33

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(config.coreKtx)
    implementation(config.appcompat)
    implementation(config.material)
    implementation(config.logger)
    implementation(config.retrofit)
    implementation(config.retrofit.converter.json)
    implementation(config.retrofit.converter.moshi)
    testImplementation(config.junit)
    androidTestImplementation(config.androidJunit)
    androidTestImplementation(config.androidEspresso.core)
}