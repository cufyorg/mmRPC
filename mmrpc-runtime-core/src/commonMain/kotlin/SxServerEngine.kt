package org.cufy.mmrpc.runtime

/**
 * Represents a simplex server engine for handling remote procedure calls (RPC) with single-directional
 * communication. It provides mechanisms to register routines for various modes of data processing
 * (unary, streaming, etc.).
 *
 * This interface extends [ServerEngine], inheriting its capabilities for handling RPC requests
 * and responses while focusing on simplex communication patterns.
 */
abstract class SxServerEngine :
    ServerEngine.N0
