package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Marker0
import org.cufy.mmrpc.Marker1
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.context.FinalStage
import kotlin.reflect.KClass

////////////////////////////////////////

typealias EmitScope = Marker0
typealias InjectScope = Marker1
typealias ContextScope = Marker3

////////////////////////////////////////

enum class GenFeature {
    /**
     * Enable debug assertions and logs.
     */
    DEBUG,

    /**
     * Generate structures that are not part of a protocol.
     */
    GENERATE_TYPES,

    /**
     * Generate structures that are part of a protocol.
     */
    GENERATE_PROTOCOLS,

    /**
     * Keep original type class names.
     */
    KEEP_TYPE_CLASS_NAMES,

    /**
     * Keep original fault class names.
     */
    KEEP_FAULT_CLASS_NAMES,

    /**
     * Keep original protocol class names.
     */
    KEEP_PROTOCOL_CLASS_NAMES,

    /**
     * Keep original routine class names.
     */
    KEEP_ROUTINE_CLASS_NAMES,

    /**
     * Keep original routine function names.
     */
    KEEP_ROUTINE_FUNCTION_NAMES,

    /**
     * Keep original field property names.
     */
    KEEP_FIELD_PROPERTY_NAMES,

    /**
     * Keep original const property names.
     */
    KEEP_CONST_PROPERTY_NAMES,

    /**
     * Keep original enum entry names.
     */
    KEEP_ENUM_ENTRY_NAMES,
}

enum class GenPackaging {
    /**
     * Use regular sub-packages for packing.
     */
    SUB_PACKAGES,
}

////////////////////////////////////////

@Suppress("serial")
open class GenException(
    message: String?,
    val refs: Set<CanonicalName?>,
    cause: Throwable? = null,
) : Exception(message, cause)

////////////////////////////////////////

data class EmitNode(
    val emission: context(FinalStage) () -> Unit,
)

data class InjectNode<T : Any>(
    val type: KClass<T>,
    val target: CanonicalName?,
    val declares: List<CanonicalName>,
    val injection: context(FinalStage) T.() -> Unit,
    val fallback: context(FinalStage) () -> Unit,
)

data class NodeList(
    val emissions: List<EmitNode>,
    val injections: List<InjectNode<*>>,
)

data class ResultList(
    val files: List<FileSpec>,
    val fails: List<GenException>,
)

////////////////////////////////////////

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

enum class TraitStrategy {
    INTERFACE,
    SEALED_INTERFACE,
}

////////////////////////////////////////
