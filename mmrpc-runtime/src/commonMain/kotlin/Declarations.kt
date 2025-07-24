package org.cufy.mmrpc

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KType

interface FaultObject {
    val canonicalName: CanonicalName
}

interface RoutineObject<I : Any, O : Any> {
    val canonicalName: CanonicalName
    val comm: Set<Comm>
    val typeI: KType
    val typeO: KType
}

@JvmInline
@Serializable
value class Comm(val value: String) {
    companion object {
        /**
         * Indicates that the communication can be performed over HTTP.
         */
        val Http = Comm("Http")

        /**
         * Indicates that the communication can be performed over the Kafka.
         */
        val Kafka = Comm("Kafka")

        /**
         * Indicates that message direction is client-to-server.
         */
        val Inbound = Comm("Inbound")

        /**
         * Indicates that message direction is server-to-client(s).
         */
        val Outbound = Comm("Outbound")

        /**
         * Requires confirmation of the identity of a client.
         */
        @Deprecated("Use a custom Comm constant instead.")
        val SameClient = Comm("SameClient")

        /**
         * Requires confirmation of the identity of a software.
         */
        @Deprecated("Use a custom Comm constant instead.")
        val SameSoftware = Comm("SameSoftware")

        /**
         * Requires confirmation of the identity of a client
         * and confirmation of consent from a subject to the
         * same client.
         */
        @Deprecated("Use a custom Comm constant instead.")
        val SameSubject = Comm("SameSubject")

        /**
         * Requires confirmation of the identity of the service.
         */
        @Deprecated("Use a custom Comm constant instead.")
        val SameService = Comm("SameService")
    }
}
