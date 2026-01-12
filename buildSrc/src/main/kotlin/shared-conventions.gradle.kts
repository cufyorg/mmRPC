plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.gradleup.tapmoc)
}

tapmoc {
    java(libs.versions.java.get().toInt())
    kotlin(libs.versions.kotlin.get())
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates(
        groupId = group.toString(),
        artifactId = project.name,
        version = version.toString(),
    )
    pom {
        name = "mmRPC"
        description = "Dsl for defining api specifications regardless of the underlying communication technology."
        inceptionYear = "2024"
        url = "https://github.com/cufyorg/mmRPC"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "LSafer"
                name = "Sulaiman Oboody"
                url = "https://github.com/LSafer/"
            }
        }
        scm {
            url = "https://github.com/cufyorg/mmRPC"
            connection = "scm:git:git://github.com/cufyorg/mmRPC.git"
            developerConnection = "scm:git:ssh://git@github.com/cufyorg/mmRPC.git"
        }
    }
}
