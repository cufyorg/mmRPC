package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.featureDebug

@Marker3
inline fun GenGroup.debug(block: () -> Unit) {
    if (ctx.featureDebug)
        block()
}

@Marker3
fun GenGroup.debugRejectAnonymous(tag: String, element: ElementDefinition) {
    if (ctx.featureDebug && element.isAnonymous)
        failGen(tag, element) { "anonymous elements are rejected" }
}

@Marker3
fun GenGroup.debugRequireGeneratedClass(tag: String, element: ElementDefinition) {
    if (ctx.featureDebug && !hasGeneratedClass(element))
        failGen(tag, element) { "elements are required to have generated class" }
}
