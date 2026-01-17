plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcRuntimeCore)

        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)
        implementation(libs.kotlin.coroutines.core)

        implementation(libs.kaftor)
        implementation(libs.kafka.clients)
        implementation(libs.kafka.streams)
    }
}
