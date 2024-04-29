// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        apply(from = "dependencies-versions.gradle")

        classpath("com.android.tools.build:gradle:8.1.2")
        classpath(libs.kotlin.gradle.plugin)
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.jakewharton:butterknife-gradle-plugin:10.1.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
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
