package org.cufy.mmrpc

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

interface FaultObject {
    val canonicalName: CanonicalName
}

interface RoutineObject<I : Any, O : Any> {
    val canonicalName: CanonicalName
    val comm: Set<Comm>
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
        val SameClient = Comm("SameClient")

        /**
         * Requires confirmation of the identity of a software.
         */
        val SameSoftware = Comm("SameSoftware")

        /**
         * Requires confirmation of the identity of a client
         * and confirmation of consent from a subject to the
         * same client.
         */
        val SameSubject = Comm("SameSubject")

        /**
         * Requires confirmation of the identity of the service.
         */
        val SameService = Comm("SameService")
    }
}
