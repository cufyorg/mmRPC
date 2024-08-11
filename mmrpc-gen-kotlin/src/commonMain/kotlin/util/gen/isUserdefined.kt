package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenGroup.isUserdefined(element: ScalarDefinition): Boolean {
    return element.canonicalName in ctx.classes &&
            element.canonicalName !in ctx.nativeElements
}

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenGroup.isUserdefined(element: MetadataDefinition): Boolean {
    return element.canonicalName in ctx.classes &&
            element.canonicalName !in ctx.nativeElements
}
