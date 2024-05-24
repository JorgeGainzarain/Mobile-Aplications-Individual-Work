// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //noinspection GradlePluginVersion
        classpath ("com.android.tools.build:gradle:2.3.1")
        classpath ("com.google.gms:google-services:4.4.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
    }