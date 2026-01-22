package org.cufy.mmrpc.runtime

/**
 * Represents a full-duplex client engine interface that supports bidirectional communication.
 *
 * This interface extends the [ClientEngine], enabling implementations to handle scenarios
 * where both the client and server can send and receive messages simultaneously over a single
 * connection. It facilitates scenarios requiring real-time updates or continuous data exchanges
 * between the client and the server.
 *
 * Key features include:
 * - Supporting flows of requests and responses.
 * - Capability to handle various interaction styles, such as request-response and streaming.
 */
abstract class FdxClientEngine : ClientEngine {
    final override fun is0Supported() = true
    final override fun is1Supported() = true
    final override fun is2Supported() = true
    final override fun is3Supported() = true
    final override fun is4Supported() = true
}
