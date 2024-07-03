package org.cufy.specdsl.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.specdsl.*

private const val TAG = "SPECDSL_GEN_KOTLIN"

enum class GenFeature {
    KOTLINX_SERIALIZATION,
    DEBUG,
}

open class GenException(
    val failure: GenFailure,
    cause: Throwable? = null,
) : Exception(failure.message, cause)

data class GenFailure(
    val group: String,
    val tag: String,
    val message: String,
    val element: ElementDefinition,
)

open class GenContext(
    val pkg: String,
    val specSheet: SpecSheet,
    val classes: Map<CanonicalName, ClassName>,
    val defaultScalarClass: ClassName?,
    val nativeElements: Set<CanonicalName>,
    val features: Set<GenFeature>,
) {
    val namespaceSet = specSheet.collectChildren()
        .filterNot { it.isAnonymous }
        .flatMap { it.asNamespace.collect() }
        .toSet()

    val failures = mutableListOf<GenException>()

    val toplevelBlocks = mutableListOf<FileSpec.Builder.() -> Unit>()

    val objectBlocks = namespaceSet.associateWith {
        mutableListOf<TypeSpec.Builder.() -> Unit>()
    }
    val objectOptionalBlocks = namespaceSet.associateWith {
        mutableListOf<TypeSpec.Builder.() -> Unit>()
    }
    val fileBlocks = namespaceSet.associateWith {
        mutableListOf<FileSpec.Builder.() -> Unit>()
    }
    val fileOptionalBlocks = namespaceSet.associateWith {
        mutableListOf<FileSpec.Builder.() -> Unit>()
    }
}

abstract class GenGroup {
    abstract val ctx: GenContext

    abstract fun apply()

    @Marker0
    inline fun failGenBoundary(block: () -> Unit) {
        try {
            block()
        } catch (e: GenException) {
            ctx.failures += e
        }
    }

    @Marker0
    fun failGen(tag: String, definition: ElementDefinition, message: () -> String): Nothing {
        val failure = GenFailure(
            group = this::class.simpleName.orEmpty(),
            tag = tag,
            message = message(),
            element = definition,
        )

        throw GenException(failure)
    }

    @Marker0
    fun onToplevel(block: FileSpec.Builder.() -> Unit) {
        ctx.toplevelBlocks += block
    }

    @Marker0
    fun onObject(ns: Namespace, block: TypeSpec.Builder.() -> Unit) {
        val list = ctx.objectBlocks[ns]
        list ?: error("$TAG: namespace not defined on initialization: ${ns.canonicalName.value}")
        list += block
    }

    @Marker0
    fun onObjectOptional(ns: Namespace, block: TypeSpec.Builder.() -> Unit) {
        val list = ctx.objectOptionalBlocks[ns]
        list ?: error("$TAG: namespace not defined on initialization: ${ns.canonicalName.value}")
        list += block
    }

    @Marker0
    fun onFile(ns: Namespace, block: FileSpec.Builder.() -> Unit) {
        val list = ctx.fileBlocks[ns]
        list ?: error("$TAG: namespace not defined on initialization: ${ns.canonicalName.value}")
        list += block
    }

    @Marker0
    fun onFileOptional(ns: Namespace, block: FileSpec.Builder.() -> Unit) {
        val list = ctx.fileOptionalBlocks[ns]
        list ?: error("$TAG: namespace not defined on initialization: ${ns.canonicalName.value}")
        list += block
    }
}
