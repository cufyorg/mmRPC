package org.cufy.mmrpc.gen.kotlin.util

internal fun String.furtherEscape(): String {
    return replace(":", "_")
}

internal fun xth(position: Int): String {
    return when (position + 1) {
        1 -> "first"
        2 -> "second"
        3 -> "third"
        4 -> "fourth"
        5 -> "fifth"
        6 -> "sixth"
        7 -> "seventh"
        8 -> "eighth"
        9 -> "ninth"
        10 -> "tenth"
        11 -> "eleventh"
        12 -> "twelfth"
        13 -> "thirteenth"
        14 -> "fourteenth"
        15 -> "fifteenth"
        16 -> "sixteenth"
        17 -> "seventeenth"
        18 -> "eighteenth"
        19 -> "nineteenth"
        20 -> "twentieth"
        else -> "_${position}th"
    }
}
