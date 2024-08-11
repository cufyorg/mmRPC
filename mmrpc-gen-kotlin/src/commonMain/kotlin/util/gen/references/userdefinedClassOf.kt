package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.debug
import org.cufy.mmrpc.gen.kotlin.util.gen.isUserdefined

private const val TAG = "userdefinedClassOf"

/**
 * Return the name of the class defined in user code that represents the given [element].
 * Assuming the [element] was declared by the user to be defined in user code.
 */
@Marker3
fun GenGroup.userdefinedClassOf(element: MetadataDefinition): ClassName {
    debug { if (!isUserdefined(element)) failGen(TAG, element) { "element not userdefined" } }
    return ctx.classes[element.canonicalName]
        ?: failGen(TAG, element) { "element class is not set" }
}

/**
 * Return the name of the class defined in user code that represents the given [element].
 * Assuming the [element] was declared by the user to be defined in user code.
 */
@Marker3
fun GenGroup.userdefinedClassOf(element: ScalarDefinition): ClassName {
    debug { if (!isUserdefined(element)) failGen(TAG, element) { "element not userdefined" } }
    return ctx.classes[element.canonicalName]
        ?: failGen(TAG, element) { "no element to userdefined-class mapping was set" }
}
