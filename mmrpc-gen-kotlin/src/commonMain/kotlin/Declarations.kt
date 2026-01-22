package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.context.FinalStage
import kotlin.reflect.KClass

////////////////////////////////////////

typealias EmitScope = Marker0
typealias InjectScope = Marker1
typealias ContextScope = Marker3

internal object Names {
    const val N0 = "N0"
    const val N0R = "N0R"
    const val N1 = "N1"
    const val N2 = "N2"
    const val N3 = "N3"
    const val N4 = "N4"
    const val SX = "Sx"
    const val SX_REFLUX = "SxReflux"
    const val HDX = "Hdx"
    const val FDX = "Fdx"
}

internal object Comms {
    val N0 = Comm.UnaryVoid
    val N0R = Comm.VoidUnary
    val N1 = Comm.UnaryUnary
    val N2 = Comm.StreamUnary
    val N3 = Comm.UnaryStream
    val N4 = Comm.StreamStream
}

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
