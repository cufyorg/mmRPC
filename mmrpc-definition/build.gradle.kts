plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    js {
        browser()
        nodejs()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    sourceSets.commonMain.dependencies {
        implementation(projects.mmrpcRuntime)

        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)

        api(libs.jetbrains.annotations)

        implementation(libs.charleskorn.kaml)
    }
}
