package org.cufy.mmrpc.gen.kotlin.util

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "calculateStrategy"

////////////////////////////////////////////////////////////

enum class ScalarStrategy {
    DATA_OBJECT,
    VALUE_CLASS,
}

@Marker3
fun GenGroup.calculateStrategy(element: ScalarDefinition): ScalarStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.canonicalName in ctx.nativeElements)
        return ScalarStrategy.DATA_OBJECT

    return ScalarStrategy.VALUE_CLASS
}

////////////////////////////////////////////////////////////

enum class MetadataStrategy {
    DATA_OBJECT,
    ANNOTATION_CLASS,
}

@Marker3
fun GenGroup.calculateStrategy(element: MetadataDefinition): MetadataStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.canonicalName in ctx.nativeElements)
        return MetadataStrategy.DATA_OBJECT

    return MetadataStrategy.ANNOTATION_CLASS
}

////////////////////////////////////////////////////////////

enum class StructStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

@Marker3
fun GenGroup.calculateStrategy(element: StructDefinition): StructStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.structFields.isEmpty())
        return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}

////////////////////////////////////////////////////////////

enum class UnionStrategy {
    DATA_OBJECT,
    ENUM_CLASS,
    SEALED_INTERFACE,
    SEALED_CLASS,
}

@Marker3
fun GenGroup.calculateStrategy(element: UnionDefinition): UnionStrategy {
    debugRejectAnonymous(TAG, element)

    if (element.unionTypes.isEmpty()) return UnionStrategy.DATA_OBJECT
    if (isConstEnumCompatible(element)) return UnionStrategy.ENUM_CLASS
    if (isSealedInterfaceCompatible(element)) return UnionStrategy.SEALED_INTERFACE

    return UnionStrategy.SEALED_CLASS
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
            is TupleDefinition,
            is InterDefinition,
            -> return false

            is ScalarDefinition,
            is UnionDefinition,
            is StructDefinition,
            -> continue
        }
    }

    return true
}

////////////////////////////////////////////////////////////
