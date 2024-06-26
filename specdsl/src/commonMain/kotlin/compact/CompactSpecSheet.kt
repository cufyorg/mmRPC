package org.cufy.specdsl.compact

import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.SpecSheet
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class CompactSpecSheet(val elements: Set<CompactElementDefinition>) {
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

    for (element in this.elements) {
        inflated[element.canonicalName] = element.inflate {
            inflated[it]?.invoke()
        }
    }

    val output = inflated.mapValues { it.value() }
    val failed = output.asSequence().filter { it.value == null }.toList()

    if (failed.isNotEmpty()) {
        error(buildString {
            append("Inflation failed: failed to inflate the following definitions: ")
            for ((canonicalName) in failed) {
                appendLine()
                append(canonicalName)
            }
        })
    }

    return SpecSheet(output.map { it.value as ElementDefinition })
}
