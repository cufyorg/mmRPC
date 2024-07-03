package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.UnionStrategy
import org.cufy.specdsl.gen.kotlin.util.calculateStrategy
import org.cufy.specdsl.gen.kotlin.util.debugRejectAnonymous
import org.cufy.specdsl.gen.kotlin.util.debugRejectNative

private const val TAG = "calculateUnionInterfaces"

@Marker0
fun GenGroup.calculateUnionInterfaces(element: ScalarDefinition): Set<ClassName> {
    debugRejectNative(TAG, element)

    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: StructDefinition): Set<ClassName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: TupleDefinition): Set<ClassName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: UnionDefinition): Set<ClassName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: InterDefinition): Set<ClassName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
private fun GenGroup.calculateUnionInterfaces0(element: TypeDefinition): Set<ClassName> {
    debugRejectAnonymous(TAG, element)

    return buildSet {
        for (it in ctx.specSheet.collectChildren()) {
            if (it !is UnionDefinition) continue
            if (it.isAnonymous) continue
            if (element !in it.unionTypes) continue

            if (calculateStrategy(it) == UnionStrategy.SEALED_INTERFACE)
                add(classOf(it))
        }
    }
}
