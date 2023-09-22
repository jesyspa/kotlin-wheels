import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm").version("1.9.0")
    id("org.jetbrains.kotlin.plugin.formver").version("1.9.255-SNAPSHOT")
    `java-library`
}

formver {
    unsupportedFeatureBehaviour("assume_unreachable")
    conversionTargetsSelection("all_targets")
}

repositories {
    maven("https://maven.vorpal-research.science")
    mavenCentral()
    mavenLocal()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalUnsignedTypes")
        freeCompilerArgs.add("-opt-in=kotlin.StdlibApi")
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/commonMain", "src/jvmMain"))
        }
    }
}