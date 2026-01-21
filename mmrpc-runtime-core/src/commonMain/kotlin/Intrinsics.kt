package org.cufy.mmrpc.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.serializer

@Suppress("FunctionName")
inline fun <I, O> _wrap_cs(
    crossinline block: suspend context(CoroutineScope) (I) -> O
): suspend (I) -> O = { coroutineScope { block(it) } }

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

context(engine: ServerEngine)
inline fun <reified Req : Any> register0(
    canonicalName: String,
    noinline handler: suspend (Req) -> Unit,
) {
    engine.register0(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        handler = handler,
    )
}

context(engine: ServerEngine)
inline fun <reified Req : Any, reified Res : Any> register1(
    canonicalName: String,
    noinline handler: suspend (Req) -> Res,
) {
    engine.register1(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
        handler = handler,
    )
}

context(engine: ServerEngine)
inline fun <reified Req : Any, reified Res : Any> register2(
    canonicalName: String,
    noinline handler: suspend (Flow<Req>) -> Res,
) {
    engine.register2(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
        handler = handler,
    )
}

context(engine: ServerEngine)
inline fun <reified Req : Any, reified Res : Any> register3(
    canonicalName: String,
    noinline handler: (Req) -> Flow<Res>,
) {
    engine.register3(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
        handler = handler,
    )
}

context(engine: ServerEngine)
inline fun <reified Req : Any, reified Res : Any> register4(
    canonicalName: String,
    noinline handler: (Flow<Req>) -> Flow<Res>,
) {
    engine.register4(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
        handler = handler,
    )
}
