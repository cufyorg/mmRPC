package org.cufy.mmrpc

import kotlin.reflect.KType

interface FaultObject {
    val canonicalName: CanonicalName
}

interface RoutineObject<I : Any, O : Any> {
    val canonicalName: CanonicalName
    val comm: Comm
    val typeI: KType
    val typeO: KType
}
