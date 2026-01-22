package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a client engine for executing remote procedure calls (RPC) with different modes
 * of data transmission (simplex, half-duplex, full-duplex).
 *
 * The [ClientEngine] interface provides methods for executing various types of RPC calls
 * (unary, stream-based, etc.) with serialization and deserialization capabilities.
 */
sealed interface ClientEngine {
    companion object;

    sealed interface N0 : ClientEngine {
        suspend fun <Req : Any> exec0(
            canonicalName: String,
            request: Req,
            reqSerial: KSerializer<Req>,
        )
    }

    sealed interface N1 : ClientEngine {
        suspend fun <Req : Any, Res : Any> exec1(
            canonicalName: String,
            request: Req,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
        ): Res
    }

    sealed interface N2 : ClientEngine {
        suspend fun <Req : Any, Res : Any> exec2(
            canonicalName: String,
            request: Flow<Req>,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
        ): Res
    }

    sealed interface N3 : ClientEngine {
        fun <Req : Any, Res : Any> exec3(
            canonicalName: String,
            request: Req,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
        ): Flow<Res>
    }

    sealed interface N4 : ClientEngine {
        fun <Req : Any, Res : Any> exec4(
            canonicalName: String,
            request: Flow<Req>,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
        ): Flow<Res>
    }
}
