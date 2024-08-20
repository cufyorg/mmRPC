package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope

/**
 * Return true, if the given [element] is supposed to have a generated class.
 */
@Marker3
fun GenScope.hasGeneratedClass(element: ElementDefinition): Boolean {
    if (element.isAnonymous) return false
    if (element is ArrayDefinition) return false
    if (element is OptionalDefinition) return false
    if (element is ScalarDefinition)
        if (isNative(element) || isUserdefined(element))
            return false
    if (element is MetadataDefinition)
        if (isNative(element) || isUserdefined(element))
            return false
    if (element is ConstDefinition)
        if (isNative(element))
            return false
    val parent = ctx.elementsNS[element.namespace] ?: return true
    return hasGeneratedClass(parent)
}
