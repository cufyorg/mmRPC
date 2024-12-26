package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ElementDefinition

enum class GenFeature {
    /**
     * Adds appropriate kotlinx serialization
     * annotations to generated code.
     */
    KOTLINX_SERIALIZATION,

    /**
     * Enable debug assertions and logs.
     */
    DEBUG,

    /**
     * Don't implicitly add builtin elements.
     */
    NO_BUILTIN,

    /**
     * Generate field definition object classes.
     */
    GEN_FIELD_OBJECTS,

    /**
     * Generate endpoint definition object classes.
     */
    GEN_ENDPOINT_OBJECTS,

    /**
     * Keep original type class names.
     */
    KEEP_TYPE_CLASS_NAMES,

    /**
     * Keep original fault class names.
     */
    KEEP_FAULT_CLASS_NAMES,

    /**
     * Keep original field property names.
     */
    KEEP_FIELD_PROPERTY_NAMES,
}

val GenContext.featureKotlinxSerialization
    get() = GenFeature.KOTLINX_SERIALIZATION in features

val GenContext.featureDebug
    get() = GenFeature.DEBUG in features

val GenContext.featureNoBuiltin
    get() = GenFeature.NO_BUILTIN in features

val GenContext.featureGenFieldObjects
    get() = GenFeature.GEN_FIELD_OBJECTS in features

val GenContext.featureGenEndpointObjects
    get() = GenFeature.GEN_ENDPOINT_OBJECTS in features

val GenContext.featureKeepTypeClassNames
    get() = GenFeature.KEEP_TYPE_CLASS_NAMES in features

val GenContext.featureKeepFaultClassNames
    get() = GenFeature.KEEP_FAULT_CLASS_NAMES in features

val GenContext.featureKeepFieldPropertyNames
    get() = GenFeature.KEEP_FIELD_PROPERTY_NAMES in features

enum class GenPackaging {
    /**
     * Use regular sub-packages for packing.
     */
    SUB_PACKAGES,
}

enum class GenRange {
    EVERYTHING,
    SHARED_ONLY,
    COMM_ONLY,
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
