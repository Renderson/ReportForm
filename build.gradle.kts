// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.build.gradle.version)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.google.service.plugin)
        classpath(libs.firebase.crashlytics.plugin)
        classpath(libs.navigation.plugin)
        classpath(libs.hilt.plugin)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("com.google.dagger.hilt.android") version "2.57" apply false
    id("com.google.devtools.ksp") version "2.1.20-2.0.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
