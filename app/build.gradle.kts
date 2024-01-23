plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.dynamiccolors"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dynamiccolors"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding.isEnabled = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0") // by ViewModels()
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6") // by ViewModels()
    implementation("com.google.dagger:dagger:2.48.1") // dagger 2
    implementation("com.google.dagger:dagger-android:2.48.1") // dagger 2
    implementation("com.google.dagger:dagger-android-support:2.48.1") // dagger 2
    kapt("com.google.dagger:dagger-android-processor:2.48.1") // dagger 2
    kapt("com.google.dagger:dagger-compiler:2.48.1") // dagger 2

    implementation("com.google.android.material:material:1.11.0") // material 3
    implementation("io.coil-kt:coil:2.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}