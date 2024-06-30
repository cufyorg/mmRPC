package org.cufy.specdsl.gradle

import org.cufy.specdsl.gradle.kotlin.SpecdslKotlinExtension
import org.gradle.api.Action
import org.gradle.api.file.FileCollection

open class SpecdslExtension {
    /**
     * SpecSheet files.
     */
    var files: FileCollection? = null

    /**
     * Input directories. (scanned for *.specdsl.json)
     *
     * Default: `["src/main/resources/", "src/commonMain/resources/"]`
     */
    var directories: FileCollection? = null

    val kotlin by lazy { SpecdslKotlinExtension() }

    /** Configuration of Kotlin DSL generation */
    fun kotlin(action: Action<SpecdslKotlinExtension>) {
        action.execute(kotlin)
    }
}
