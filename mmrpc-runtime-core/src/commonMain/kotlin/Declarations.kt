package org.cufy.mmrpc.runtime

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

@RequiresOptIn
annotation class ExperimentalMmrpcApi

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

fun FaultException.toFaultObject(): FaultObject {
    return FaultObject(
        canonicalName = canonicalName,
        message = message,
    )
}

fun FaultObject.toFaultException(cause: Throwable? = null): FaultException {
    return FaultException(canonicalName, message, cause)
}

abstract class ServerContext : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<ServerContext>

    override val key get() = Key
    abstract val canonicalName: String
}

suspend fun serverContext(): ServerContext {
    return currentCoroutineContext()[ServerContext]
        ?: error("Server context is not available")
}

abstract class ClientContext : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<ClientContext>

    override val key get() = Key
    abstract val canonicalName: String
}

suspend fun clientContext(): ClientContext {
    return currentCoroutineContext()[ClientContext]
        ?: error("Client context is not available")
}
