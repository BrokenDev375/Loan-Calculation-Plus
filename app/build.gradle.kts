plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp) // cáº§n cho Room vÃ  Hilt
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.loancaculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.loancaculator"
        minSdk = 28
        targetSdk = 35
        versionCode = 15
        versionName = "1.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Keystore THáº¬T vga31b (nguá»“n: keystore/VGA31B/keystore.jks + pass.txt). app/keystore/ gitignored.
            storeFile = file("keystore/update.jks")
            storePassword = "879a575e60786dfcb51e003527e6ce32"
            keyAlias = "92edac7bae24550d696e793f6b351190"
            keyPassword = "cc46878523f4fbcb8810e8df0249c268"
        }
    }

    buildTypes {
        debug {
            // Lib tháº­t cÃ³ meta-data firebase_crashlytics_collection_enabled=${crashlyticsCollectionEnabled}
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
        }
        release {
            isMinifyEnabled = true          // Báº®T BUá»˜C theo guide tÃ­ch há»£p
            isShrinkResources = true        // Báº®T BUá»˜C (Ä‘i cáº·p vá»›i minifyEnabled)
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "true"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/atomicfu.kotlin_module"
        }
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {
    // Base-application AAR (ads / IAP / splash / language / notification shell)
    // Cung cáº¥p transitively: play-services-ads 25.2.0, ump 4.0.0, mediation.facebook 6.21.0.2,
    // af-android-sdk 6.18.0, glide 4.16.0, jsoup 1.21.2, okhttp 5.x, billing 7.1.0, firebase BoM 34.1.0.
    api(project(":base-application"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.review.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room (history quÃ©t)
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Coil â€” load icon app (thay base64 cá»§a RN)
    implementation("io.coil-kt:coil-compose:2.0.0")

    // Pager cho mÃ n Intro dÃ¹ng androidx.compose.foundation.pager (Ä‘Ã£ cÃ³ trong Compose BoM).
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutines & StateFlow
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Firebase BoM â€” unified with base-application (34.1.0)
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")

    // Billing (premium gating; nguá»“n sá»± tháº­t lÃ  IAPUtils cá»§a lib)
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // In-app update (RN dÃ¹ng expo-in-app-updates)
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:app-update:2.1.0")

    // Gson + Retrofit (remote config data / version check helpers)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Install Referrer
    implementation("com.android.installreferrer:installreferrer:2.2")
}

