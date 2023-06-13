// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.internal.jvm.Jvm

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle")
    }
}
plugins {
//    id("com.android.application") version "7.4.0" apply false
//    id("com.android.library") version "7.4.0" apply false
//    id("org.jetbrains.kotlin.android") version "1.7.21" apply false
//    id("org.jetbrains.kotlin.jvm") version "1.7.21" apply false
    alias(config.plugins.android.application) apply false
    alias(config.plugins.android.library) apply false
    alias(config.plugins.kotlin.android) apply false
    alias(config.plugins.kotlin.jvm) apply false
}