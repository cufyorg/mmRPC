package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.*

private const val TAG = "strategy.kt"

@Marker3
context(ctx: GenContext)
fun InterDefinition.calculateStrategy(): InterStrategy {
    debugRequireGeneratedClass(TAG, this)

    if (types.all { it.fields.isEmpty() })
        return InterStrategy.DATA_OBJECT

    return InterStrategy.DATA_CLASS
}

@Marker3
context(ctx: GenContext)
fun StructDefinition.calculateStrategy(): StructStrategy {
    debugRequireGeneratedClass(TAG, this)

    if (fields.isEmpty() && fieldsInherited().isEmpty())
        return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}

@Marker3
context(ctx: GenContext)
fun TupleDefinition.calculateStrategy(): TupleStrategy {
    debugRequireGeneratedClass(TAG, this)

    if (types.isEmpty())
        return TupleStrategy.DATA_OBJECT

    return TupleStrategy.DATA_CLASS
}

@Marker3
context(ctx: GenContext)
fun UnionDefinition.calculateStrategy(): UnionStrategy {
    debugRequireGeneratedClass(TAG, this)

    if (types.isEmpty()) return UnionStrategy.DATA_OBJECT

    val pkg = canonicalName.generatedPackageName()

    for (it in types) {
        if (pkg != it.canonicalName.generatedPackageName())
            return UnionStrategy.WRAPPER_SEALED_INTERFACE
    }

    return UnionStrategy.SEALED_INTERFACE
}

@Marker3
context(ctx: GenContext)
fun TraitDefinition.calculateStrategy(): TraitStrategy {
    debugRequireGeneratedClass(TAG, this)

    val structs = collectStructs()

    if (structs.isEmpty()) return TraitStrategy.SEALED_INTERFACE

    val pkg = canonicalName.generatedPackageName()

    for (it in structs) {
        if (pkg != it.canonicalName.generatedPackageName())
            return TraitStrategy.INTERFACE
    }

    return TraitStrategy.SEALED_INTERFACE
}
