package org.cufy.mmrpc.gen.kotlin.util

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

private fun convertCamelCase(name: String, delimiter: Char): String {
    return buildString(name.length * 2) {
        var bufferedChar: Char? = null
        var previousUpperCharsCount = 0

        name.forEach { c ->
            if (c.isUpperCase()) {
                if (previousUpperCharsCount == 0 && isNotEmpty() && last() != delimiter)
                    append(delimiter)

                bufferedChar?.let(::append)

                previousUpperCharsCount++
                bufferedChar = c.lowercaseChar()
            } else {
                if (bufferedChar != null) {
                    if (previousUpperCharsCount > 1 && c.isLetter()) {
                        append(delimiter)
                    }
                    append(bufferedChar)
                    previousUpperCharsCount = 0
                    bufferedChar = null
                }
                append(c)
            }
        }

        if (bufferedChar != null) {
            append(bufferedChar)
        }
    }
}
