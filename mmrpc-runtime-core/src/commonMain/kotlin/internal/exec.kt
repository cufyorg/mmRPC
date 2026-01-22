package org.cufy.mmrpc.runtime.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.serializer
import org.cufy.mmrpc.runtime.ClientEngine

suspend inline fun <reified Req : Any> exec0(
    engine: ClientEngine,
    canonicalName: String,
    request: Req,
) {
    engine.exec0(
        canonicalName = canonicalName,
        request = request,
        reqSerial = serializer<Req>(),
    )
}

suspend inline fun <reified Req : Any, reified Res : Any> exec1(
    engine: ClientEngine,
    canonicalName: String,
    request: Req,
): Res {
    return engine.exec1(
        canonicalName = canonicalName,
        request = request,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
    )
}

suspend inline fun <reified Req : Any, reified Res : Any> exec2(
    engine: ClientEngine,
    canonicalName: String,
    request: Flow<Req>,
): Res {
    return engine.exec2(
        canonicalName = canonicalName,
        request = request,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
    )
}

inline fun <reified Req : Any, reified Res : Any> exec3(
    engine: ClientEngine,
    canonicalName: String,
    request: Req,
): Flow<Res> {
    return engine.exec3(
        canonicalName = canonicalName,
        request = request,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
    )
}

inline fun <reified Req : Any, reified Res : Any> exec4(
    engine: ClientEngine,
    canonicalName: String,
    request: Flow<Req>,
): Flow<Res> {
    return engine.exec4(
        canonicalName = canonicalName,
        request = request,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
    )
}
