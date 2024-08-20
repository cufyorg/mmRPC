package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope

/**
 * Return true, if the given [element] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
fun GenScope.isNative(element: ScalarDefinition): Boolean {
    return element.canonicalName in ctx.nativeElements
}

/**
 * Return true, if the given [element] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
fun GenScope.isNative(element: MetadataDefinition): Boolean {
    return element.canonicalName in ctx.nativeElements
}

/**
 * Return true, if the given [element] was declared
 * native (fully inlined) by the user.
 */
@Marker3
fun GenScope.isNative(element: ConstDefinition): Boolean {
    return element.canonicalName in ctx.nativeElements
}
