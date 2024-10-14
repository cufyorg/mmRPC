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
                implementation(libs.kotlin.serialization.json)

                implementation(libs.ktor.server.core)

                implementation(libs.kafka.clients)
                implementation(libs.kafka.streams)

                implementation(libs.kaftor)
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
    }
}
