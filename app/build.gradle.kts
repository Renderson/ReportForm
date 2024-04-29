plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 0

fun computeVersionName() = "$versionMajor.$versionMinor.$versionPatch"

android {
    namespace = "com.rendersoncs.report"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rendersoncs.report"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = computeVersionName()
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.9.10"
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    testImplementation("junit:junit:4.13.2")

    // Android
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")

    // Android IU
    implementation(libs.android.ui.activity)
    implementation(libs.android.ui.material)
    implementation(libs.android.ui.fragment)
    implementation(libs.android.ui.constrant.layout)
    implementation(libs.android.ui.recyclerview)
    implementation(libs.android.ui.cardview)

    // Compose
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation(libs.compose.runtime)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling)

    // Compose HorizontalPager
    implementation(libs.compose.horizontal.pager)

    // volley http library
    implementation(libs.volley)
    implementation(libs.gson)

    // NavHeader Circle
    implementation("de.hdodenhof:circleimageview:3.0.1")

    // Glide
    implementation(libs.glide)

    // Graphic
    implementation(libs.chart)

    // IText PDF
    implementation(libs.itext)

    // Apache
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.commons:commons-collections4:4.4")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)

    // Facebook Auth API
    implementation("com.facebook.android:facebook-android-sdk:4.42.0")

    // Google Auth API
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // About api
    implementation("com.github.medyo:android-about-page:1.2.5")

    // Leak Canary - Detect memory leaks in code
    //debugImplementation "com.squareup.leakcanary:leakcanary-android:$versions.leakcanary"
    //releaseImplementation "com.squareup.leakcanary:leakcanary-support-fragment:$versions.leakcanary"

    // Signature Pad
    //implementation("com.github.gcacace:signature-pad:1.2.1")

    // Navigation library
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)
    implementation(libs.coroutine.play.services)

    // Coroutine Lifecycle Scopes
    implementation(libs.coroutine.viewmodel)
    implementation(libs.coroutine.rumtime)
    implementation(libs.coroutine.livedata)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // CameraX core library
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // Shimmer
    implementation(libs.shimmer)

    // Lottie
    implementation(libs.lottie.compose)

    // Adb mob
    implementation(libs.google.ads)
}
