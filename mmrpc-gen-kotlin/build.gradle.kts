plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mmrpc-core"))
                implementation(project(":mmrpc-definition"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.core)

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
