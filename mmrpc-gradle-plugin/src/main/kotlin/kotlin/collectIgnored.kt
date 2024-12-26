package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.SpecSheet
import org.cufy.mmrpc.gen.kotlin.GenRange

fun collectIgnored(specSheet: SpecSheet, range: GenRange): Set<CanonicalName> {
    if (range == GenRange.EVERYTHING) return emptySet()

    val commRoots = specSheet.elements.asSequence()
        .filter { it is ProtocolDefinition || it is RoutineDefinition }
        .map { it.canonicalName.asNamespace }
        .toSet()

    if (range == GenRange.SHARED_ONLY) {
        return specSheet.elements.asSequence()
            .map { it.canonicalName.asNamespace }
            .filter { elementCN ->
                commRoots.any { rootCN ->
                    elementCN == rootCN || elementCN in rootCN
                }
            }
            .map { it.canonicalName }
            .toSet()
    }

    if (range == GenRange.COMM_ONLY) {
        return specSheet.elements.asSequence()
            .map { it.canonicalName.asNamespace }
            .filter { elementCN ->
                commRoots.none { rootCN ->
                    elementCN == rootCN || elementCN in rootCN
                }
            }
            .map { it.canonicalName }
            .toSet()
    }

    return emptySet()
}
