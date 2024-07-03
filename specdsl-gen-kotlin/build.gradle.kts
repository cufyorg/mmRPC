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
                implementation(project(":specdsl-core"))
                implementation(project(":specdsl-definition"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.core)

                implementation(libs.kotlinpoet)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
