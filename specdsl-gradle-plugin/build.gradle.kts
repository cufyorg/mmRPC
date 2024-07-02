plugins {
    `maven-publish`
    `java-gradle-plugin`

    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.plugin.publish)
}

gradlePlugin {
    plugins {
        create("SpecdslPlugin") {
            id = "org.cufy.specdsl"
            implementationClass = "org.cufy.specdsl.gradle.SpecdslPlugin"
            displayName = "Code generators for Specdsl schema files"
            description = "Code generators for Specdsl schema files"
            tags.set(listOf("kotlin"))
        }
    }
}

dependencies {
    implementation(project(":specdsl-definition"))
    implementation(project(":specdsl-gen-kotlin"))

    implementation(kotlin("stdlib"))
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.kotlinpoet)

    api("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
}
