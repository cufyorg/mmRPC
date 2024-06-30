package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.Marker0
import org.cufy.specdsl.StructDefinition
import org.cufy.specdsl.gen.kotlin.GenGroup

private const val TAG = "calculateStructStrategy"

enum class StructStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

@Marker0
fun GenGroup.calculateStructStrategy(element: StructDefinition): StructStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.structFields.isEmpty()) return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}
