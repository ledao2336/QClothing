plugins {
    alias(libs.plugins.android.application)    // CHỈ CÒN MỘT KHAI BÁO PLUGIN APPLICATION
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.qclothing"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.qclothing"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.mediarouter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add Firebase Firestore dependencies here
    implementation(platform("com.google.firebase:firebase-bom:33.9.0")) // Firebase BOM - QUAN TRỌNG GIỮ LẠI
    implementation("com.google.firebase:firebase-firestore-ktx")         // THAY THẾ analytics BẰNG firestore-ktx (hoặc firebase-firestore nếu dùng Java)
    // XÓA dòng này (nếu bạn không cần Firebase Analytics): implementation("com.google.firebase:firebase-analytics")
}