plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")


}

android {
    namespace = "com.example.answerandquestion"
    compileSdk = 31


    buildFeatures {
        viewBinding
    }



    defaultConfig {
        applicationId = "com.example.answerandquestion"
        minSdk = 21
        targetSdk = 31
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.mukeshsolanki:android-otpview-pinview:2.1.2")
    implementation ("com.github.User:Repo:Version")
    // Import the BoM for the Firebase platform
    implementation ("com.google.firebase:firebase-bom:32.5.0")

    // Declare the dependencies for the desired Firebase products without specifying versions
    // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.google.firebase:firebase-messaging")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.pgreze:android-reactions:1.6")
    implementation ("com.devlomi:circularstatusview:1.0.1")
    implementation ("com.github.OMARIHAMZA:StoryView:1.0.2-alpha")
    implementation ("com.github.sharish:ShimmerRecyclerView:v1.3")
    implementation ("com.github.mukeshsolanki.android-otpview-pinview:otpview-compose:3.1.0")
    implementation ("com.github.mukeshsolanki.android-otpview-pinview:otpview:3.1.0")


















}