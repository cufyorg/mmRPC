package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.gen.kotlin.GenFeature
import org.cufy.specdsl.gen.kotlin.GenGroup

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
