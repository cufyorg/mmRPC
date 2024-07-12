package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ElementDefinition

enum class GenFeature {
    KOTLINX_SERIALIZATION,
    DEBUG,
    NO_BUILTIN,
}

val GenContext.featureKotlinxSerialization
    get() = GenFeature.KOTLINX_SERIALIZATION in features

val GenContext.featureDebug
    get() = GenFeature.DEBUG in features

val GenContext.featureNoBuiltin
    get() = GenFeature.NO_BUILTIN in features

enum class GenPackaging {
    /**
     * Use regular sub-packages for packing.
     */
    SUB_PACKAGES,
}

@Suppress("serial")
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

data class CreateElementNode(
    val element: ElementDefinition,
    val block: () -> TypeSpec.Builder,
)

data class OnElementNode(
    val element: ElementDefinition?,
    val block: TypeSpec.Builder.() -> Unit,
)
