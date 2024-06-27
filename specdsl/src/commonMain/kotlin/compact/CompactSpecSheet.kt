package org.cufy.specdsl.compact

import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.SpecSheet
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class CompactSpecSheet(val elements: Set<CompactElementDefinition> = emptySet()) {
    operator fun plus(element: CompactElementDefinition) =
        CompactSpecSheet(this.elements + element)

    operator fun plus(elements: Iterable<CompactElementDefinition>) =
        CompactSpecSheet(this.elements + elements)

    operator fun plus(specSheet: CompactSpecSheet) =
        CompactSpecSheet(this.elements + specSheet.elements)
}

fun SpecSheet.toCompact(): CompactSpecSheet {
    return CompactSpecSheet(
        elements = collectChildren()
            .map { it.toCompact() }
            .toSet()
    )
}

fun CompactSpecSheet.inflate(): SpecSheet {
    val inflated = mutableMapOf<CanonicalName, () -> ElementDefinition?>()
    val requested = mutableListOf<CanonicalName>()

    for (element in this.elements) {
        inflated[element.canonicalName] = element.inflate {
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
