package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

sealed interface ClientEngine {
    interface Http : ClientEngine
    interface Kafka : ClientEngine
    interface Custom : ClientEngine
    companion object

    fun is0Supported() = false
    fun is1Supported() = false
    fun is2Supported() = false
    fun is3Supported() = false
    fun is4Supported() = false

    suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        error("$this doesn't support: (Req) -> Unit")
    }

    suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        error("$this doesn't support: (Req) -> Res")
    }

    suspend fun <Req : Any, Res : Any> exec2(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        error("$this doesn't support: (Flow<Req>) -> Res")
    }

    fun <Req : Any, Res : Any> exec3(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> {
        error("$this doesn't support: (Req) -> Flow<Res>")
    }

    fun <Req : Any, Res : Any> exec4(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> {
        error("$this doesn't support: (Flow<Req>) -> Flow<Res>")
    }
}
