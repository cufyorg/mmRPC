package org.cufy.mmrpc.runtime

import kotlinx.serialization.Serializable

open class FaultException(
    val canonicalName: String,
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

@Serializable
data class FaultObject(
    val canonicalName: String,
    val message: String? = null,
)
