plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcRuntime)
        implementation(projects.mmrpcDefinition)

        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect", "2.0.0"))
        implementation(libs.kotlin.serialization.json)

        implementation(libs.kotlinpoet)

        implementation(libs.pearx.kasechange)
    }
}
