package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "nativeClassOf"

@Marker3
fun GenGroup.nativeClassOf(element: MetadataDefinition): ClassName {
    return ctx.classes[element.canonicalName]
        ?: failGen(TAG, element) { "element class is not set" }
}

@Marker3
fun GenGroup.nativeClassOf(element: ScalarDefinition): ClassName {
    return ctx.classes[element.canonicalName]
        ?: ctx.defaultScalarClass
        ?: failGen(TAG, element) { "element class is not set nor a default class" }
}
