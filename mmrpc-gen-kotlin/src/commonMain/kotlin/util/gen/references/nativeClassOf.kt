package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.debug
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative

private const val TAG = "nativeClassOf"

/**
 * Return the name of the native class that represents the given [element].
 * Assuming the [element] was declared by the user to be a native kotlin class.
 */
@Marker3
fun GenScope.nativeClassOf(element: MetadataDefinition): ClassName {
    debug { if (!isNative(element)) failGen(TAG, element) { "element not native" } }
    return ctx.nativeMetadataClasses[element.canonicalName]
        ?: failGen(TAG, element) { "element class is not set" }
}

/**
 * Return the name of the native class that represents the given [element].
 * Assuming the [element] was declared by the user to be a native kotlin class.
 */
@Marker3
fun GenScope.nativeClassOf(element: ScalarDefinition): ClassName {
    debug { if (!isNative(element)) failGen(TAG, element) { "element not native" } }
    return ctx.nativeScalarClasses[element.canonicalName]
        ?: failGen(TAG, element) { "no element to native class mapping" }
}
