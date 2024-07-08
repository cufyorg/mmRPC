package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asClassName
import org.cufy.mmrpc.gen.kotlin.util.debugRejectAnonymous

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
fun GenGroup.classOf(element: ScalarDefinition): ClassName {
    debugRejectAnonymous(TAG, element)

    if (element.canonicalName in ctx.nativeElements)
        return nativeClassOf(element)

    return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
}

@Marker0
fun GenGroup.classOf(element: UnionDefinition): ClassName {
    debugRejectAnonymous(TAG, element)

    return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
}

@Marker0
fun GenGroup.classOf(element: StructDefinition): ClassName {
    debugRejectAnonymous(TAG, element)

    return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
}
