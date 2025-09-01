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
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mmrpc-runtime"))
                implementation(project(":mmrpc-definition"))

                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect", "2.0.0"))
                implementation(libs.kotlin.serialization.json)

                implementation(libs.kotlinpoet)

                implementation(libs.pearx.kasechange)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
