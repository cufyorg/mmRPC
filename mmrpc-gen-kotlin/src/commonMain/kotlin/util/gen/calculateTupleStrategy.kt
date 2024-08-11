package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "calculateTupleStrategy"

enum class TupleStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

@Marker3
fun GenGroup.calculateTupleStrategy(element: TupleDefinition): TupleStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.tupleTypes.isEmpty())
        return TupleStrategy.DATA_OBJECT

    return TupleStrategy.DATA_CLASS
}
