plugins {
    id("com.android.application")
}

android {
    namespace = "com.rao.multiaccountcredentials"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rao.multiaccountcredentials"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.8.4")
    implementation("androidx.navigation:navigation-ui:2.8.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    //
    implementation ("com.google.android.material:material:1.9.0")  // Check for latest version


    //gif
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.28")


    //api

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.loopj.android:android-async-http:1.4.11")


    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //
    implementation ("commons-io:commons-io:2.8.0")



    //
    implementation ("androidx.camera:camera-core:1.2.0")
    implementation ("androidx.camera:camera-camera2:1.2.0")
    implementation ("androidx.camera:camera-lifecycle:1.2.0")
//    implementation ("androidx.camera:camera-view:1.0.0")
    implementation("androidx.camera:camera-view:1.0.0-alpha26")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

    //
//    implementation ("com.github.smarteist:autoimageslider:1.4.0")

}