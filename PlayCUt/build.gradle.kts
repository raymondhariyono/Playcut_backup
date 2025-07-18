// File: build.gradle.kts (Project)
plugins {
    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.kotlin.android) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false

    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false

    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
}