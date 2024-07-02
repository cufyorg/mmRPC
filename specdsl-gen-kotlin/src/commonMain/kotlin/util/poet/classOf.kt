package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.debugRejectAnonymous

/*
classOf:

returns the globally accessible class of some element.
*/

private const val TAG = "classOf"

@Marker0
fun GenGroup.classOf(namespace: Namespace): ClassName {
    return ClassName(ctx.pkg, namespace.asClassName)
}

@Marker0
fun GenGroup.classOf(element: MetadataDefinition): ClassName {
    debugRejectAnonymous(TAG, element)

    if (element.canonicalName in ctx.nativeElements)
        return nativeClassOf(element)

    return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
}

@Marker0
fun GenGroup.classOf(element: FaultDefinition): ClassName {
    debugRejectAnonymous(TAG, element)

    return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
}

@Marker0
fun GenGroup.classOf(element: TypeDefinition): TypeName {
    debugRejectAnonymous(TAG, element)

    val asClassName = when (element) {
        is ScalarDefinition -> {
            if (element.canonicalName in ctx.nativeElements)
                return nativeClassOf(element)

            element.asClassName
        }

        is UnionDefinition -> element.asClassName
        is InterDefinition -> element.asClassName
        is StructDefinition -> element.asClassName
        is TupleDefinition -> element.asClassName

        is OptionalDefinition,
        is ArrayDefinition,
        is ConstDefinition,
        -> failGen(TAG, element) { "element not supported" }
    }

    return ClassName(ctx.pkg, element.namespace.asClassName, asClassName)
}
