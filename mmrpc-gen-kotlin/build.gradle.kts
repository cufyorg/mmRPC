plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mmrpc-runtime"))
                implementation(project(":mmrpc-definition"))

                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
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
