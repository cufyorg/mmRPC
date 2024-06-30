package org.cufy.specdsl.gen.kotlin.util

internal fun String.trainCase(): String {
    // examples: "HELLO_WORLD_WIDE_WEB3"
    if (none { it.isLetter() && it.isUpperCase() }) return uppercase()
    // examples: "hello_world_wide_web3"
    if (none { it.isLetter() && it.isLowerCase() }) return this

    // examples: "hello_WorldWideWeb3"
    val buffer = StringBuilder()

    repeat(this.length) it@{ i ->
        val c = this[i]
        val cp = this.getOrNull(i - 1)

        if (!c.isUpperCase() || cp == null || !cp.isLetter() || !cp.isLowerCase()) {
            buffer.append(c.uppercase())
            return@it
        }

        buffer.append("_")
        buffer.append(c)
    }

    return buffer.toString()
}
