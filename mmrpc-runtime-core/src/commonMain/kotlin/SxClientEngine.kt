package org.cufy.mmrpc.runtime

/**
 * Represents a simplex operational mode client engine.
 *
 * This interface is a subtype of [ClientEngine] that defines a communication
 * pattern where data flows in a single direction, either as requests or
 * responses, but not simultaneously in both directions.
 *
 * Implementations of this interface are expected to adhere to the simplex
 * communication model, ensuring data flow is restricted to one direction
 * per interaction.
 */
abstract class SxClientEngine :
    ClientEngine.N0
