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
                implementation(kotlin("stdlib"))

                api("com.github.lsafer-meemer.jetbrains-annotations:jetbrains-annotations:be20592be9")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
