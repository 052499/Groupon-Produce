plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Firebase plugin
    id("org.jetbrains.kotlin.android") // Required if you're using Kotlin
}

android {
    namespace = "com.example.grouponproduceapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.grouponproduceapp"
        minSdk = 28
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
    // Firebase BoM for consistent versioning
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    // Other dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
