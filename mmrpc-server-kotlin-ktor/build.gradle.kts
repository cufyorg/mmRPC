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

                implementation("io.ktor:ktor-server-core:2.3.12")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
