package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a server-side engine that provides mechanisms for handling various
 * communication patterns, including unary and streaming. This interface serves
 * as a foundation for implementing simplex, half-duplex, and full-duplex
 * server engines.
 *
 * The engine supports pattern registrations with customized serialization and
 * processing logic. It also includes utility methods to check the supported
 * operations.
 */
sealed interface ServerEngine {
    companion object;

    sealed interface N0 : ServerEngine {
        fun <Req : Any> register0(
            canonicalName: String,
            reqSerial: KSerializer<Req>,
            handler: suspend (Req) -> Unit,
        )
    }

    sealed interface N1 : ServerEngine {
        fun <Req : Any, Res : Any> register1(
            canonicalName: String,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
            handler: suspend (Req) -> Res,
        )
    }

    sealed interface N2 : ServerEngine {
        fun <Req : Any, Res : Any> register2(
            canonicalName: String,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
            handler: suspend (Flow<Req>) -> Res,
        )
    }

    sealed interface N3 : ServerEngine {
        fun <Req : Any, Res : Any> register3(
            canonicalName: String,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
            handler: (Req) -> Flow<Res>,
        )
    }

    sealed interface N4 : ServerEngine {
        fun <Req : Any, Res : Any> register4(
            canonicalName: String,
            reqSerial: KSerializer<Req>,
            resSerial: KSerializer<Res>,
            handler: (Flow<Req>) -> Flow<Res>,
        )
    }
}
