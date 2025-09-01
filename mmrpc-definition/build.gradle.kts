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
                implementation(project(":mmrpc-runtime"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.json)

                api("org.jetbrains:annotations:26.0.2")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
