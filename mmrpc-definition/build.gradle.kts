import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_0
        languageVersion = KotlinVersion.KOTLIN_2_0
        coreLibrariesVersion = KotlinVersion.KOTLIN_2_0.version
    }
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
                implementation(kotlin("test", "2.0.0"))
            }
        }
    }
}
