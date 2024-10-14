plugins {
    `maven-publish`
    `java-gradle-plugin`

    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.plugin.publish)
}

gradlePlugin {
    plugins {
        create("MMRPCPlugin") {
            id = "org.cufy.mmrpc"
            implementationClass = "org.cufy.mmrpc.gradle.MMRPCPlugin"
            displayName = "Code generators for mmRPC schema files"
            description = "Code generators for mmRPC schema files"
            // tags.set(listOf("kotlin"))
        }
    }
}

dependencies {
    implementation(project(":mmrpc-core"))
    implementation(project(":mmrpc-definition"))
    implementation(project(":mmrpc-gen-kotlin"))

    implementation(kotlin("stdlib"))
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)

    api(libs.kotlin.gradle.plugin)

    implementation(libs.kotlinpoet)

    implementation(libs.charleskorn.kaml)
}
