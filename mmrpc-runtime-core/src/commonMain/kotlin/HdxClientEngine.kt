package org.cufy.mmrpc.runtime

/**
 * Represents a Half-Duplex client engine.
 *
 * This interface is designed for implementing client engines that follow
 * a half-duplex communication model. Half-duplex communication allows data
 * to flow in one direction at a time, meaning that sending and receiving
 * operations cannot occur simultaneously.
 *
 * It extends the [ClientEngine] interface, providing the necessary foundation
 * for handling various client-side communication mechanisms, such as executing
 * requests and managing streams.
 *
 * The implementing class should specify the supported modes and handle
 * corresponding execution logic for one-way or bidirectional communication
 * patterns within the constraints of a half-duplex operation.
 */
abstract class HdxClientEngine :
    ClientEngine.N0,
    ClientEngine.N1
