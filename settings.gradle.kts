pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "mmrpc"

include("mmrpc")

// include directories that starts with "mmrpc-"
for (file in rootDir.listFiles().orEmpty()) {
    if (file.isDirectory && file.name.startsWith("mmrpc-")) {
        include(":${file.name}")
    }
}
