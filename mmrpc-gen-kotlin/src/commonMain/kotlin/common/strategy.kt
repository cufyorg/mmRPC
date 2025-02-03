package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.*

private const val TAG = "strategy.kt"

@Marker3
fun GenScope.calculateInterStrategy(element: InterDefinition): InterStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.types.all { it.fields.isEmpty() })
        return InterStrategy.DATA_OBJECT

    return InterStrategy.DATA_CLASS
}

@Marker3
fun GenScope.calculateStructStrategy(element: StructDefinition): StructStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.fields.isEmpty())
        return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}

@Marker3
fun GenScope.calculateTupleStrategy(element: TupleDefinition): TupleStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.types.isEmpty())
        return TupleStrategy.DATA_OBJECT

    return TupleStrategy.DATA_CLASS
}

@Marker3
fun GenScope.calculateUnionStrategy(element: UnionDefinition): UnionStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.types.isEmpty()) return UnionStrategy.DATA_OBJECT

    val pkg = generatedPackageOf(element.canonicalName)

    for (it in element.types) {
        if (pkg != generatedPackageOf(it.canonicalName))
            return UnionStrategy.WRAPPER_SEALED_INTERFACE
    }

    return UnionStrategy.SEALED_INTERFACE
}
