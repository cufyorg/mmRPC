plugins {
    `java-gradle-plugin`
    `gradle-plugin-conventions`
}

gradlePlugin {
    plugins {
        create("MmrpcPlugin") {
            id = "org.cufy.mmrpc"
            implementationClass = "org.cufy.mmrpc.gradle.MmrpcPlugin"
            displayName = "Code generators for mmRPC schema files"
            description = "Code generators for mmRPC schema files"
            // tags.set(listOf("kotlin"))
        }
    }
}

dependencies {
    implementation(projects.mmrpcDefinition)
    implementation(projects.mmrpcGenKotlin)

    implementation(kotlin("stdlib"))
    implementation(libs.kotlin.serialization.json)

    api(libs.kotlin.gradle.plugin)

    implementation(libs.kotlinpoet)

    implementation(libs.charleskorn.kaml)
}
