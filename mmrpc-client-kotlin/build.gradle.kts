plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcDefinition)

        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)

        implementation(libs.ktor.client.core)

        implementation(libs.josekt)
        implementation(libs.extkt.json)
        implementation(libs.extkt.crypto)
    }
    sourceSets.jvmMain.dependencies {
        implementation(libs.kafka.clients)
        implementation(libs.kafka.streams)
    }
}
