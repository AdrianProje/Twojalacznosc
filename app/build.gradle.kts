plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("C:\\key\\keyandroidstudio\\keysign.jks")
            storePassword = "Je8evqa#Lwnq92#tVwq9Wvt"
            keyAlias = "keyrelease"
            keyPassword = "Je8evqa#Lwnq92#tVwq9Wvt"
        }
    }
    namespace = "com.ak.twojetlimc"
    compileSdk = 35

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applicationId = "com.ak.twojetlimc"
        versionCode = 2
        vectorDrawables {
            useSupportLibrary = true
        }
        targetSdk = 35
        versionName = "Ciuty - 0.51.1"
    }

    buildTypes {
        release {
            isShrinkResources = false
            isMinifyEnabled = false
            // Opcje wyłączone ze względu na błędy z biblioteką skrapeit,
            // rozwiązać później TODO("Naprawić błąd minifyEnabled")
            //Funckja zmniejsza rozmiar apk lub bundle
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isJniDebuggable = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dependenciesInfo {
        includeInBundle = true
        includeInApk = true
    }
    buildToolsVersion = "35.0.1"
    ndkVersion = "28.0.13004108"
    flavorDimensions += listOf("BaseApp")
    productFlavors {
        create("Galaxy") {
            dimension = "BaseApp"
            minSdk = 31
        }
        create("Pixel") {
            dimension = "BaseApp"
            versionNameSuffix = "Pixel"
            minSdk = 26
        }
    }

    buildFeatures {
        compose = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,DEPENDENCIES,INDEX.LIST}"
            excludes += "mozilla/public-suffix-list.txt"
        }
    }
}

dependencies {
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.test.junit4)
    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.material)
    implementation(libs.javax.servlet.api)
    implementation(libs.mimedir)
    implementation(libs.gson)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.preference)
    implementation(libs.skrapeit) {
        exclude(group = "it.skrape", module = "skrapeit-async-fetcher")
    }

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.androidx.annotation)
    implementation(libs.javax.annotation.api)
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.glance.material)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}