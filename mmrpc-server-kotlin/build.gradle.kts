plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcDefinition)

        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)

        implementation(libs.ktor.server.core)

        implementation(libs.kafka.clients)
        implementation(libs.kafka.streams)

        implementation(libs.kaftor)
        implementation(libs.josekt)
        implementation(libs.extkt.json)
        implementation(libs.extkt.crypto)
    }
}
