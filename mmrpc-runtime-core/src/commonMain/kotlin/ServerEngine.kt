package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface ServerEngine {
    interface Http : ServerEngine
    interface Kafka : ServerEngine
    interface Custom : ServerEngine
    companion object

    fun is0Supported() = false
    fun is1Supported() = false
    fun is2Supported() = false
    fun is3Supported() = false
    fun is4Supported() = false

    fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit,
    ) {
        error("$this doesn't support: (Req) -> Unit")
    }

    fun <Req : Any, Res : Any> register1(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Res,
    ) {
        error("$this doesn't support: (Req) -> Res")
    }

    fun <Req : Any, Res : Any> register2(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Flow<Req>) -> Res,
    ) {
        error("$this doesn't support: (Flow<Req>) -> Res")
    }

    fun <Req : Any, Res : Any> register3(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Flow<Res>,
    ) {
        error("$this doesn't support: (Req) -> Flow<Res>")
    }

    fun <Req : Any, Res : Any> register4(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Flow<Req>) -> Flow<Res>,
    ) {
        error("$this doesn't support: (Flow<Req>) -> Flow<Res>")
    }
}
