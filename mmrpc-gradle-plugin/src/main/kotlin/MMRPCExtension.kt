package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gradle.kotlin.MMRPCKotlinExtension
import org.gradle.api.Action
import org.gradle.api.file.FileCollection

open class MMRPCExtension {
    /**
     * SpecSheet files.
     */
    var files: FileCollection? = null

    /**
     * Input directories.
     *
     * Supported Extensions:
     * - **.mmrpc.json
     * - **.mmrpc.yaml
     * - **.mmrpc.yml
     *
     * Default: `["src/main/resources/", "src/commonMain/resources/"]`
     */
    var directories: FileCollection? = null

    val kotlin by lazy { MMRPCKotlinExtension() }

    /** Configuration of Kotlin DSL generation */
    fun kotlin(action: Action<MMRPCKotlinExtension>) {
        action.execute(kotlin)
    }
}
