package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenScope

@Marker3
inline fun GenScope.debug(block: () -> Unit) {
    if (GenFeature.DEBUG in ctx.features)
        block()
}

@Marker3
fun GenScope.debugRequireGeneratedClass(tag: String, element: ElementDefinition) {
    if (GenFeature.DEBUG in ctx.features && !hasGeneratedClass(element))
        fail(tag, element) { "elements are required to have generated class" }
}

@Marker3
fun GenScope.debugRequireGeneratedClass(tag: String, canonicalName: CanonicalName) {
    if (GenFeature.DEBUG in ctx.features) {
        val element = ctx.elements.find { it.canonicalName == canonicalName }

        if (element == null || !hasGeneratedClass(element))
            fail(tag, element) { "elements are required to have generated class" }
    }
}

fun GenScope.debugLog(tag: String, msg: String) {
    if (GenFeature.DEBUG !in ctx.features) return

    println("mmRPG: $tag: $msg".colored(fg = 36, bg = 40))
}

// https://stackoverflow.com/a/77677280/22235255
private fun String.colored(fg: Int, bg: Int, bold: Boolean = false): String {
    return if (bold) "\u001B[${fg};${bg};1m$this\u001B[0m"
    else "\u001B[${fg};${bg}m$this\u001B[0m"
}
