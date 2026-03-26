plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.lostandfound"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lostandfound"
        minSdk        = 24
        targetSdk     = 34
        versionCode   = 2
        versionName   = "2.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}
