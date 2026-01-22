plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcRuntimeCore)

        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.protobuf)
        implementation(libs.kotlin.coroutines.core)

        implementation(libs.protobuf)
        implementation(libs.protobuf.lite)
        implementation(libs.protobuf.kotlin)
        implementation(libs.grpc.api)
        implementation(libs.grpc.stub)
        implementation(libs.grpc.kotlin.stub)
    }
}
