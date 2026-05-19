@file:Suppress("UnstableApiUsage")

import com.lagradost.cloudstream3.gradle.CloudstreamExtension

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.lagradost.cloudstream3.gradle")
}

android {
    namespace = "com.akwamtube"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
}

cloudstream {
    setRepo("https://raw.githubusercontent.com/donotworrydna-prog/Akwam-Cloudstream/main/plugins.json")
    authors = listOf("donotworrydna-prog")
    description = "إضافة لموقع أكوام تيوب - as.akwam.tube"
    language = "ar"
    version = "1.0.0"
    status = 1
}

dependencies {
    implementation("com.lagradost:cloudstream3:pre-release")
    implementation("org.jsoup:jsoup:1.17.2")
}
