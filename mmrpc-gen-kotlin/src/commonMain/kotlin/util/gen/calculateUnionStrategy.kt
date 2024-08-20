package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedPackageOf

private const val TAG = "calculateUnionStrategy"

enum class UnionStrategy {
    DATA_OBJECT,
    SEALED_INTERFACE,
    WRAPPER_SEALED_INTERFACE,
}

@Marker3
fun GenScope.calculateUnionStrategy(element: UnionDefinition): UnionStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.unionTypes.isEmpty()) return UnionStrategy.DATA_OBJECT

    val pkg = generatedPackageOf(element)

    for (it in element.unionTypes) {
        if (pkg != generatedPackageOf(it))
            return UnionStrategy.WRAPPER_SEALED_INTERFACE
    }

    return UnionStrategy.SEALED_INTERFACE
}
