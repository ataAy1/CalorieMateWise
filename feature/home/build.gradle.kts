plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.navigation.safe.args)
    id("kotlin-kapt")
}

android {
    namespace = "com.app.home"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures{
        viewBinding=true
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

    buildFeatures{
        viewBinding=true
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

    implementation(project(":feature:meal-planning"))
    implementation(project(":feature:search"))
    implementation(project(":feature:search-interactive"))
    implementation(project(":feature:profile"))

    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //firebase
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)


    // MPAndroidChart
    implementation(libs.mpandroidchart)

    //Coil
    implementation(libs.coil)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}