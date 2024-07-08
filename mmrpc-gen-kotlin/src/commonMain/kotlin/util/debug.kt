package org.cufy.mmrpc.gen.kotlin.util

import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker0
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker0
inline fun GenGroup.debug(block: () -> Unit) {
    if (GenFeature.DEBUG in ctx.features)
        block()
}

@Marker0
fun GenGroup.debugRejectAnonymous(tag: String, element: ElementDefinition) {
    debug {
        if (element.isAnonymous)
            failGen(tag, element) { "anonymous elements are rejected" }
    }
}

@Marker0
fun GenGroup.debugRejectNative(tag: String, element: ElementDefinition) {
    debug {
        if (element.canonicalName in ctx.nativeElements)
            failGen(tag, element) { "native elements are rejected" }
    }
}
