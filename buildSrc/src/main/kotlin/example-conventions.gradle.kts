plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradleup.tapmoc)
}

tapmoc {
    java(libs.versions.java.get().toInt())
    kotlin(libs.versions.kotlin.get())
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
