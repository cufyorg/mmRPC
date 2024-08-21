package org.cufy.mmrpc.client

import org.cufy.hash.md5ToString
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject

fun <I : StructObject> generateKey(routine: RoutineObject<I, *>, input: I): String? {
    val names = routine.__info__.key ?: return null
    val inputMap = input.toMap()
    return names.joinToString(";") { inputMap[it].toString() }.md5ToString()
}
