plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
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
        versionCode = 1
        vectorDrawables {
            useSupportLibrary = true
        }
        targetSdk = 35
        versionName = "Ciuty - 0.50.7.2"
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
    ndkVersion = "27.2.12479018"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    //noinspection UseTomlInstead
    implementation("androidx.compose.ui:ui:1.7.6")
    //noinspection UseTomlInstead
    implementation("androidx.compose.ui:ui-graphics:1.7.6")
    //noinspection UseTomlInstead
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")
    //noinspection UseTomlInstead
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.preference)
    implementation(libs.skrapeit) {
        exclude(group = "it.skrape", module = "skrapeit-http-fetcher")
    }
    implementation(libs.gson)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.androidx.annotation)
    implementation(libs.javax.annotation.api)
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.skrapeit.browser.fetcher)
    implementation(libs.ui.test.manifest)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.glance.material)
    implementation(libs.androidx.glance.appwidget)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20230300))
    //noinspection UseTomlInstead
    implementation("androidx.compose.ui:ui-test-junit4:1.7.6")
    //noinspection UseTomlInstead
    implementation("androidx.compose.ui:ui-tooling:1.7.6")
    //noinspection UseTomlInstead
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.6")
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