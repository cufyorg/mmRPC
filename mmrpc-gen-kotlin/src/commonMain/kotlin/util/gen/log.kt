package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.featureDebug

fun GenScope.debugLog(tag: String, msg: String) {
    if (!ctx.featureDebug) return

    println("mmRPG: $tag: $msg".colored(fg = 36, bg = 40))
}

// https://stackoverflow.com/a/77677280/22235255
private fun String.colored(fg: Int, bg: Int, bold: Boolean = false): String {
    return if (bold) "\u001B[${fg};${bg};1m$this\u001B[0m"
    else "\u001B[${fg};${bg}m$this\u001B[0m"
}
