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

                implementation("org.cufy.serialization:cufyorg-json:a82f624ef6")
                implementation("org.cufy.serialization:cufyorg-jose:a82f624ef6")
                implementation("org.cufy.serialization:cufyorg-crypto:a82f624ef6")

                implementation("org.apache.kafka:kafka-clients:3.7.0")
                implementation("org.cufy.kotlin-kafka-routing:kotlin-kafka-routing:1c349988f2")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
