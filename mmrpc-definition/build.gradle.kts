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
        browser {
            binaries.library()
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mmrpc-runtime"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
