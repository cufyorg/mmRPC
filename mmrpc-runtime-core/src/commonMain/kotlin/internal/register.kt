package org.cufy.mmrpc.runtime.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.serializer
import org.cufy.mmrpc.runtime.ServerEngine

typealias Handler0<Req> = suspend (Req) -> Unit
typealias Handler1<Req, Res> = suspend (Req) -> Res
typealias Handler2<Req, Res> = suspend (Flow<Req>) -> Res
typealias Handler3<Req, Res> = (Req) -> Flow<Res>
typealias Handler4<Req, Res> = (Flow<Req>) -> Flow<Res>

context(engine: ServerEngine)
inline fun <reified Req : Any> register0(
    canonicalName: String,
    noinline handler: Handler0<Req>,
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
    noinline handler: Handler1<Req, Res>,
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
    noinline handler: Handler2<Req, Res>,
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
    noinline handler: Handler3<Req, Res>,
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
    noinline handler: Handler4<Req, Res>,
) {
    engine.register4(
        canonicalName = canonicalName,
        reqSerial = serializer<Req>(),
        resSerial = serializer<Res>(),
        handler = handler,
    )
}
