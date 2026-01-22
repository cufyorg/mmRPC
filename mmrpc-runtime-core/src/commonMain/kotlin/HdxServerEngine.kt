package org.cufy.mmrpc.runtime

/**
 * Represents a half-duplex server engine for handling remote procedure calls (RPC).
 *
 * The `Hdx` interface extends the [ServerEngine] interface, providing capabilities specific
 * to half-duplex communication. In half-duplex communication, data transmission occurs
 * in one direction at a time, alternating between sending and receiving.
 *
 * This interface should be implemented to support RPC mechanisms that require such
 * communication patterns.
 */
abstract class HdxServerEngine :
    ServerEngine.N0,
    ServerEngine.N1
