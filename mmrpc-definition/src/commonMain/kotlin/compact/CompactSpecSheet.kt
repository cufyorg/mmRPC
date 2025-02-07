package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.SpecSheet
import kotlin.js.JsName
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class CompactSpecSheet(val elements: Set<CompactElementDefinition> = emptySet()) {
    companion object {
        fun Iterable<CompactElementDefinition>.toCompactSpecSheet() = CompactSpecSheet(this)
        fun Sequence<CompactElementDefinition>.toCompactSpecSheet() = CompactSpecSheet(asIterable())
    }

    constructor(vararg elements: CompactElementDefinition) : this(elements.toSet())
    constructor(elements: Iterable<CompactElementDefinition>) : this(elements.toSet())

    operator fun plus(element: CompactElementDefinition) =
        CompactSpecSheet(this.elements + element)

    operator fun plus(elements: Iterable<CompactElementDefinition>) =
        CompactSpecSheet(this.elements + elements)

    operator fun plus(specSheet: CompactSpecSheet) =
        CompactSpecSheet(this.elements + specSheet.elements)
}

fun CompactSpecSheet.strip(): CompactSpecSheet {
    return CompactSpecSheet(elements.map {
        when (it) {
            is CompactConstDefinition -> it.copy(description = "")
            is CompactFaultDefinition -> it.copy(description = "")
            is CompactFieldDefinition -> it.copy(description = "")
            is CompactMetadataDefinition -> it.copy(description = "")
            is CompactProtocolDefinition -> it.copy(description = "")
            is CompactRoutineDefinition -> it.copy(description = "")
            is CompactArrayDefinition -> it.copy(description = "")
            is CompactEnumDefinition -> it.copy(description = "")
            is CompactInterDefinition -> it.copy(description = "")
            is CompactOptionalDefinition -> it.copy(description = "")
            is CompactScalarDefinition -> it.copy(description = "")
            is CompactStructDefinition -> it.copy(description = "")
            is CompactTupleDefinition -> it.copy(description = "")
            is CompactUnionDefinition -> it.copy(description = "")
        }
    })
}

fun SpecSheet.toCompact(): CompactSpecSheet {
    return CompactSpecSheet(
        elements = collectChildren()
            .map { it.toCompact() }
            .sortedBy { it.canonical_name.value }
            .toSet()
    )
}

fun CompactSpecSheet.inflate(): SpecSheet {
    @JsName("a")
    data class LazyInflate(
        @JsName("c")
        var compact: CompactElementDefinition? = null,
        @JsName("b")
        var element: ElementDefinition? = null,
    )

    val lazyMap = this.elements.associate { it.canonical_name to LazyInflate(it) }
    val requested = mutableListOf<CanonicalName>()

    fun LazyInflate.tryInflate(): ElementDefinition? {
        fun onLookup(canonicalName: CanonicalName): ElementDefinition? {
            requested += canonicalName
            return lazyMap[canonicalName]?.tryInflate()
        }

        if (this.element != null) return this.element
        val inflated = compact?.inflateOrNull(::onLookup) ?: return null
        this.element = inflated
        return inflated
    }

    for (it in lazyMap)
        it.value.tryInflate()

    val failed = buildList {
        for (it in requested) if (it !in lazyMap) add(it)
        for (it in lazyMap) if (it.value.element == null) add(it.key)
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

    return SpecSheet(lazyMap.map { it.value.element!! })
}
