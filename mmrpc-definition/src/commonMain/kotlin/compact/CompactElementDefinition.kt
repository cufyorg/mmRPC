package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*
import kotlin.js.JsName

@Suppress("PropertyName")
@Serializable
sealed interface CompactElementDefinition {
    val canonical_name: CanonicalName

    val description: String
    val metadata: List<CompactMetadataDefinitionUsage>

    val name get() = canonical_name.name
    val namespace get() = canonical_name.namespace

    fun isBuiltin() = builtin.elements.any { it.canonicalName == canonical_name }
}

fun ElementDefinition.toCompact(): CompactElementDefinition {
    return when (this) {
        is ArrayDefinition -> toCompact()
        is ConstDefinition -> toCompact()
        is EnumDefinition -> toCompact()
        is FaultDefinition -> toCompact()
        is FieldDefinition -> toCompact()
        is InterDefinition -> toCompact()
        is MetadataDefinition -> toCompact()
        is OptionalDefinition -> toCompact()
        is ProtocolDefinition -> toCompact()
        is RoutineDefinition -> toCompact()
        is ScalarDefinition -> toCompact()
        is StructDefinition -> toCompact()
        is TupleDefinition -> toCompact()
        is UnionDefinition -> toCompact()
    }
}

fun CompactElementDefinition.strip(): CompactElementDefinition {
    return when (this) {
        is CompactConstDefinition -> copy(description = "")
        is CompactFaultDefinition -> copy(description = "")
        is CompactFieldDefinition -> copy(description = "")
        is CompactMetadataDefinition -> copy(description = "")
        is CompactProtocolDefinition -> copy(description = "")
        is CompactRoutineDefinition -> copy(description = "")
        is CompactArrayDefinition -> copy(description = "")
        is CompactEnumDefinition -> copy(description = "")
        is CompactInterDefinition -> copy(description = "")
        is CompactOptionalDefinition -> copy(description = "")
        is CompactScalarDefinition -> copy(description = "")
        is CompactStructDefinition -> copy(description = "")
        is CompactTupleDefinition -> copy(description = "")
        is CompactUnionDefinition -> copy(description = "")
    }
}

fun CompactElementDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): ElementDefinition? {
    return when (this) {
        is CompactArrayDefinition -> inflateOrNull(onLookup)
        is CompactConstDefinition -> inflateOrNull(onLookup)
        is CompactEnumDefinition -> inflateOrNull(onLookup)
        is CompactFaultDefinition -> inflateOrNull(onLookup)
        is CompactFieldDefinition -> inflateOrNull(onLookup)
        is CompactInterDefinition -> inflateOrNull(onLookup)
        is CompactMetadataDefinition -> inflateOrNull(onLookup)
        is CompactOptionalDefinition -> inflateOrNull(onLookup)
        is CompactProtocolDefinition -> inflateOrNull(onLookup)
        is CompactRoutineDefinition -> inflateOrNull(onLookup)
        is CompactScalarDefinition -> inflateOrNull(onLookup)
        is CompactStructDefinition -> inflateOrNull(onLookup)
        is CompactTupleDefinition -> inflateOrNull(onLookup)
        is CompactUnionDefinition -> inflateOrNull(onLookup)
    }
}

fun Sequence<CompactElementDefinition>.inflate(
    builtins: Iterable<ElementDefinition>,
): Sequence<ElementDefinition> {
    @JsName("a")
    data class LazyInflate(
        @JsName("c")
        var compact: CompactElementDefinition? = null,
        @JsName("b")
        var element: ElementDefinition? = null,
    )

    val builtinsMap = builtins.associateBy { it.canonicalName }
    val elementsMap = this.associate { it.canonical_name to LazyInflate(it) }
    val requested = mutableSetOf<CanonicalName>()

    fun LazyInflate.tryInflate(): ElementDefinition? {
        fun onLookup(canonicalName: CanonicalName): ElementDefinition? {
            builtinsMap[canonicalName]?.let { return it }
            requested += canonicalName
            return elementsMap[canonicalName]?.tryInflate()
        }

        if (this.element != null) return this.element
        val inflated = compact?.inflateOrNull(::onLookup) ?: return null
        this.element = inflated
        return inflated
    }

    for (it in elementsMap)
        it.value.tryInflate()

    val failed = buildList {
        for (it in requested) if (it !in elementsMap) add(it)
        for (it in elementsMap) if (it.value.element == null) add(it.key)
    }

    if (failed.isNotEmpty()) {
        error(buildString {
            append("Inflation failed: failed to inflate the following definitions: ")
            for (it in failed) {
                appendLine()
                append("- ")
                append(it.value)
            }
        })
    }

    return elementsMap.asSequence().map { it.value.element!! }
}
