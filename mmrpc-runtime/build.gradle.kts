import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js { browser { binaries.library() } }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser { binaries.library() } }
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
