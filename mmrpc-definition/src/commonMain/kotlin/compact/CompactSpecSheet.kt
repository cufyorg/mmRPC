package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.SpecSheet
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
    val inflated = mutableMapOf<CanonicalName, () -> ElementDefinition?>()
    val requested = mutableListOf<CanonicalName>()

    for (element in this.elements) {
        inflated[element.canonical_name] = element.inflate {
            requested += it
            inflated[it]?.invoke()
        }
    }

    val output = inflated.mapValues { it.value() }
    val failed = buildList {
        addAll(requested.filter { it !in output })
        addAll(output.asSequence().filter { it.value == null }.map { it.key }.toList())
    }

    if (failed.isNotEmpty()) {
        error(buildString {
            append("Inflation failed: failed to inflate the following definitions: ")
            for (canonicalName in failed) {
                appendLine()
                append("- ")
                append(canonicalName.value)
            }
        })
    }

    return SpecSheet(output.map { it.value as ElementDefinition })
}
