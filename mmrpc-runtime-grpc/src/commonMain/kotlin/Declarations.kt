package org.cufy.mmrpc.runtime.grpc

import io.grpc.*
import kotlinx.coroutines.Dispatchers
import org.cufy.mmrpc.runtime.*
import org.cufy.mmrpc.runtime.grpc.internal.applyGrpcMmrpcServer
import org.cufy.mmrpc.runtime.grpc.internal.createGrpcMmrpcClient
import kotlin.coroutines.CoroutineContext

////////////////////////////////////////

class GrpcServerContext(
    val call: ServerCall<*, *>,
    val headers: Metadata,
    override val canonicalName: String,
) : ServerContext()

class GrpcClientContext(
    override val canonicalName: String,
) : ClientContext() {
    val request = Request()

    class Request {
        var options: CallOptions = CallOptions.DEFAULT
        val headers: Metadata = Metadata()
    }
}

suspend fun grpcServerContext(): GrpcServerContext {
    return serverContext() as? GrpcServerContext
        ?: error("Grpc server context is not available")
}

suspend fun grpcClientContext(): GrpcClientContext {
    return clientContext() as? GrpcClientContext
        ?: error("Grpc client context is not available")
}

////////////////////////////////////////

@OptIn(ExperimentalMmrpcApi::class)
context(server: ServerBuilder<*>)
fun mmrpc(
    context: CoroutineContext = Dispatchers.IO,
    block: GrpcServerEngine.Builder.() -> Unit,
) {
    applyGrpcMmrpcServer(server, context, block)
}

fun ManagedChannel.mmrpc(
    block: GrpcClientEngine.Builder.() -> Unit = {},
): GrpcClientEngine {
    return createGrpcMmrpcClient(this, block)
}

////////////////////////////////////////
