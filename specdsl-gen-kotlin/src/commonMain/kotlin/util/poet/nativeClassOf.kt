package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.MetadataDefinition
import org.cufy.specdsl.ScalarDefinition
import org.cufy.specdsl.gen.kotlin.GenGroup

private const val TAG = "nativeClassOf"

@Marker0
fun GenGroup.nativeClassOf(element: MetadataDefinition): ClassName {
    return ctx.classes[element.canonicalName]
        ?: failGen(TAG, element) { "element class is not set" }
}

@Marker0
fun GenGroup.nativeClassOf(element: ScalarDefinition): ClassName {
    return ctx.classes[element.canonicalName]
        ?: ctx.defaultScalarClass
        ?: failGen(TAG, element) { "element class is not set nor a default class" }
}
