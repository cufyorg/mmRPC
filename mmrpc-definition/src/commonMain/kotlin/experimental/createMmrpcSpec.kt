package org.cufy.mmrpc.experimental

import org.cufy.mmrpc.*
import org.cufy.mmrpc.compact.toCompact

fun createMmrpcSpec(
    name: String,
    version: String,
    sections: List<CanonicalName>,
    elements: List<ElementDefinition>,
): MmrpcSpec {
    return MmrpcSpec(
        name = name,
        version = version,
        sections = sections,
        elements = elements
            .asSequence()
            .flatMap { it.collect() }
            .filter { it.canonicalName !in builtin }
            .distinctBy { it.canonicalName }
            .sortedBy { it.canonicalName }
            .map { it.toCompact() }
            .toList()
    )
}
