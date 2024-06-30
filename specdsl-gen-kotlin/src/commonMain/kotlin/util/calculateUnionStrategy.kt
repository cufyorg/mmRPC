package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

private const val TAG = "calculateUnionStrategy"

enum class UnionStrategy {
    DATA_OBJECT,
    ENUM_CLASS,
    SEALED_INTERFACE,
    WRAPPER_CLASS,
}

@Marker0
fun GenGroup.calculateUnionStrategy(element: UnionDefinition): UnionStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.unionTypes.isEmpty()) return UnionStrategy.DATA_OBJECT
    if (isConstEnumCompatible(element)) return UnionStrategy.ENUM_CLASS
    if (isSealedInterfaceCompatible(element)) return UnionStrategy.SEALED_INTERFACE

    return UnionStrategy.WRAPPER_CLASS
}

private fun GenGroup.isConstEnumCompatible(element: UnionDefinition): Boolean {
    if (element.unionTypes.isEmpty()) return false
    if (element.unionTypes.any { it !is ConstDefinition }) return false

    @Suppress("UNCHECKED_CAST")
    val unionTypes = element.unionTypes as List<ConstDefinition>
    val commonType = unionTypes.first().constType

    return unionTypes.all { it.constType == commonType }
}

private fun GenGroup.isSealedInterfaceCompatible(element: UnionDefinition): Boolean {
    if (element.unionTypes.isEmpty()) return false

    for (it in element.unionTypes) {
        if (it.isAnonymous) return false
        if (it.canonicalName in ctx.nativeElements) return false

        when (it) {
            is ConstDefinition,
            is OptionalDefinition,
            is ArrayDefinition,
            -> return false

            is ScalarDefinition,
            is UnionDefinition,
            is StructDefinition,
            is InterDefinition,
            is TupleDefinition,
            -> continue
        }
    }

    return true
}
