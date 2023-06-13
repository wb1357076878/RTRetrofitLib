plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.rtnewnetworklib"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.rtnewnetworklib"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

//    implementation("androidx.core:core-ktx:1.7.0")
    implementation(config.coreKtx)
    implementation(config.appcompat)
    implementation(config.material)
    implementation(config.constraintLayout)
    implementation(config.navigationFragmentKtx)
    implementation(config.navigationUiKtx)
    testImplementation(config.junit)
    androidTestImplementation(config.androidJunit)
    androidTestImplementation(config.androidEspresso.core)
    implementation(projects.rtretrofitlib)
    implementation(config.retrofit)
    implementation(config.retrofit.converter.json)
    implementation(config.retrofit.converter.moshi)
    implementation(config.retrofit.converter.jaxb)
    implementation(config.retrofit.converter.simpleXml)
    implementation(config.simpleXml)
    implementation(config.mockwebserver)
    implementation(config.logger)
}