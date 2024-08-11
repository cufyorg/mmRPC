package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "primitiveClassOf"

/**
 * Return the name of the class that actually stores the values of the given [element].
 */
@Marker3
fun GenGroup.primitiveClassOf(element: ScalarDefinition): ClassName {
    return ctx.classes[element.canonicalName]
        ?: ctx.defaultScalarClass
        ?: failGen(TAG, element) { "element class is not set nor a default class" }
}
