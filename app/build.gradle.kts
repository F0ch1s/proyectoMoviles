plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.proyecto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyecto"
        minSdk = 30
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firestore (Base de datos en tiempo real)
    implementation("com.google.firebase:firebase-firestore:24.9.1")

    // Glide para cargar imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
