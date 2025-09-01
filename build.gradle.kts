plugins {
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
}

group = "org.cufy"
version = "local_snapshot"

tasks.wrapper {
    gradleVersion = "8.14"
}

subprojects {
    group = "org.cufy.mmrpc"

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
