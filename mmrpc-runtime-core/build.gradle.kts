plugins {
    `shared-conventions`
}

kotlin {
    jvm()
    sourceSets.commonMain.dependencies {
        implementation(kotlin("stdlib"))
        implementation(libs.kotlin.serialization.json)
        implementation(libs.kotlin.coroutines.core)
    }
}
