package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.TypeName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.*

private const val TAG = "calculateUnionInterfaces"

@Marker0
fun GenGroup.calculateUnionInterfaces(element: ScalarDefinition): Set<TypeName> {
    debugRejectNative(TAG, element)

    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: StructDefinition): Set<TypeName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: TupleDefinition): Set<TypeName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: UnionDefinition): Set<TypeName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
fun GenGroup.calculateUnionInterfaces(element: InterDefinition): Set<TypeName> {
    return calculateUnionInterfaces0(element)
}

@Marker0
private fun GenGroup.calculateUnionInterfaces0(element: TypeDefinition): Set<TypeName> {
    debugRejectAnonymous(TAG, element)

    return buildSet {
        main@ for (it in ctx.specSheet.collectChildren()) {
            if (it !is UnionDefinition) continue
            if (it.isAnonymous) continue
            if (element !in it.unionTypes) continue

            if (calculateUnionStrategy(it) == UnionStrategy.SEALED_INTERFACE)
                add(classOf(it))
        }
    }
}
