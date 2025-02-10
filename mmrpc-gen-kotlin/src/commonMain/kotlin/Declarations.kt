package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberSpecHolder
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.CanonicalName
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
     * Generate field name constant properties.
     */
    GEN_FIELD_NAME_PROPERTIES,

    /**
     * Generate const value constant properties.
     */
    GEN_CONST_VALUE_PROPERTIES,

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
    val element: ElementDefinition?,
)

data class CreateTypeNode(
    val canonicalName: CanonicalName,
    val block: () -> TypeSpec.Builder,
)

data class InjectTypeNode(
    val canonicalName: CanonicalName,
    val block: TypeSpec.Builder.() -> Unit,
)

data class InjectFileNode(
    val canonicalName: CanonicalName?,
    val block: FileSpec.Builder.() -> Unit,
)

data class InjectScopeNode(
    val canonicalName: CanonicalName?,
    val block: MemberSpecHolder.Builder<*>.() -> Unit,
)

enum class InterStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

enum class StructStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

enum class TupleStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

enum class UnionStrategy {
    DATA_OBJECT,
    SEALED_INTERFACE,
    WRAPPER_SEALED_INTERFACE,
}
