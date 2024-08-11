package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "calculateInterStrategy"

enum class InterStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

@Marker3
fun GenGroup.calculateInterStrategy(element: InterDefinition): InterStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.interTypes.all { it.structFields.isEmpty() })
        return InterStrategy.DATA_OBJECT

    return InterStrategy.DATA_CLASS
}
