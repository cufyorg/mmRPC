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
        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)
    }
}
