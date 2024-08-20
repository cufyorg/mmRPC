package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenScope.isUserdefined(element: ScalarDefinition): Boolean {
    return element.canonicalName in ctx.userdefinedScalarClasses
}

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenScope.isUserdefined(element: MetadataDefinition): Boolean {
    return element.canonicalName in ctx.userdefinedMetadataClasses
}
