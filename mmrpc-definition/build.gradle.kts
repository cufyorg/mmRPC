plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mmrpc-runtime"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.core)

                compileOnly("org.jetbrains:annotations:26.0.2")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
