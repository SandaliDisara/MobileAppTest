plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.appdeviceconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appdeviceconnect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")        // Latest AppCompat version
    implementation("com.google.android.material:material:1.12.0") // Latest Material Components

    // Retrofit and Gson dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")      // Retrofit for network calls
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Retrofit converter for Gson
    implementation("com.google.code.gson:gson:2.8.8")            // Gson library for JSON parsing

    testImplementation("junit:junit:4.13.2")                     // JUnit for unit tests
    androidTestImplementation("androidx.test.ext:junit:1.2.1")    // Latest AndroidX JUnit
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Latest Espresso
}