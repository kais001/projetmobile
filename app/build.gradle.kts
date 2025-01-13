plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tn.esprit.cov"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.esprit.cov"
        minSdk = 23
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


        // RecyclerView
        implementation (libs.recyclerview)

        // Room Database
        implementation (libs.room.runtime)
        annotationProcessor (libs.room.compiler)

        // Retrofit
        implementation (libs.retrofit2.retrofit)
        implementation (libs.converter.gson)



    implementation (libs.osmdroid.osmdroid.android)
    implementation (libs.play.services.maps)
    implementation (libs.osmdroid.wms)




}