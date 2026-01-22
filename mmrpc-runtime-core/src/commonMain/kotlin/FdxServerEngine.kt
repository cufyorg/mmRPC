package org.cufy.mmrpc.runtime

/**
 * Represents a full-duplex communication engine that allows bidirectional streaming
 * of requests and responses between client and server in a remote procedure call (RPC) context.
 *
 * This interface extends the [ServerEngine], enabling it to handle full-duplex transmission modes
 * where both parties can simultaneously send and receive data in a stream-based manner.
 *
 * It is typically implemented in scenarios where high-performance, asynchronous, and parallel communication
 * is required, such as in real-time applications or distributed systems.
 */
abstract class FdxServerEngine :
    ServerEngine.N0,
    ServerEngine.N1,
    ServerEngine.N2,
    ServerEngine.N3,
    ServerEngine.N4
