// File: build.gradle.kts (Project)
plugins {
    // Pastikan versi AGP cocok dengan yang di libs.versions.toml
    alias(libs.plugins.android.application) apply false

    // Pastikan versi Kotlin cocok
    alias(libs.plugins.kotlin.android) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false

    // Pastikan versi Hilt cocok
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    // KSP harus cocok dengan versi Kotlin Anda
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}