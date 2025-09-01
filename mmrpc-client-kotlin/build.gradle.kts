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
                implementation(libs.kotlin.serialization.json)

                implementation(libs.ktor.client.core)

                implementation(libs.josekt)
                implementation(libs.extkt.json)
                implementation(libs.extkt.crypto)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kafka.clients)
                implementation(libs.kafka.streams)
            }
        }
    }
}
