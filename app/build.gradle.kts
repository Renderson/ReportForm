import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties
import java.io.FileInputStream

plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 2

fun computeVersionName() = "$versionMajor.$versionMinor.$versionPatch"

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

detekt {
    toolVersion = "1.23.0" // Especifica a versão do Detekt
    config = files("config/detekt/detekt.yml") // Caminho para o arquivo de configuração personalizado
    buildUponDefaultConfig = true // Utiliza as regras padrão do Detekt como base
    allRules = false // Ativa todas as regras (inclusive instáveis) se definido como true
    parallel = true // Executa a análise em paralelo para melhorar o desempenho

    reports {
        html.required.set(true) // Gera um relatório em HTML
        xml.required.set(true)  // Gera um relatório em XML
        txt.required.set(false) // Desativa o relatório em TXT
        sarif.required.set(false) // Gera relatório no formato SARIF, útil para integração com ferramentas como GitHub
    }

    //baseline = file("config/detekt/baseline.xml") // Define um arquivo de baseline para ignorar problemas conhecidos
}

tasks.withType<Detekt> {
    jvmTarget = "1.8" // Define a versão do Java que será usada pelo Detekt
}

android {
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    namespace = "com.rendersoncs.report"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rendersoncs.report"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = computeVersionName()
        multiDexEnabled = true
        signingConfig = signingConfigs.getByName("release")
    }

    flavorDimensions.add("version")
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"
            manifestPlaceholders["appName"] = "Report Free"
        }
        create("paid") {
            dimension = "version"
            applicationIdSuffix = ".paid"
            versionNameSuffix = "-paid"
            manifestPlaceholders["appName"] = "Report"
        }
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
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit)

    // Android
    implementation(libs.androidx.appcompact)
    implementation(libs.androidx.lifecycler.runtime)

    // Android IU
    implementation(libs.android.ui.activity)
    implementation(libs.android.ui.material)
    implementation(libs.android.ui.fragment)
    implementation(libs.android.ui.constrant.layout)
    implementation(libs.android.ui.recyclerview)
    implementation(libs.android.ui.cardview)

    // Compose
    implementation(libs.compose.activity)
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
    implementation(libs.hdodenhof.cicler.image)

    // Glide
    implementation(libs.glide)

    // Graphic
    implementation(libs.chart)

    // IText PDF
    implementation(libs.itext)

    // Apache
    implementation(libs.apache.io)
    implementation(libs.apache.lang)
    implementation(libs.apache.collections)

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
    implementation(libs.facebook.auth)

    // Google Auth API
    implementation(libs.google.auth)

    // About api
    implementation(libs.about.page)

    // Leak Canary - Detect memory leaks in code
    //debugImplementation "com.squareup.leakcanary:leakcanary-android:$versions.leakcanary"
    //releaseImplementation "com.squareup.leakcanary:leakcanary-support-fragment:$versions.leakcanary"

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

    // Detekt
    detektPlugins(libs.detekt)
}
