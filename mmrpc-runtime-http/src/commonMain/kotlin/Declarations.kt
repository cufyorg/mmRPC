package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.cufy.mmrpc.runtime.ClientContext
import org.cufy.mmrpc.runtime.ServerContext
import org.cufy.mmrpc.runtime.clientContext
import org.cufy.mmrpc.runtime.http.internal.applyHttpMmrpcServer
import org.cufy.mmrpc.runtime.http.internal.createHttpMmrpcClient
import org.cufy.mmrpc.runtime.serverContext

////////////////////////////////////////

class HttpServerContext(
    val call: ApplicationCall,
    override val canonicalName: String,
) : ServerContext()

class HttpClientContext(
    override val canonicalName: String,
) : ClientContext() {
    lateinit var request: HttpRequestBuilder
    lateinit var response: HttpResponse
}

suspend fun httpServerContext(): HttpServerContext {
    return serverContext() as? HttpServerContext
        ?: error("Http server context is not available")
}

suspend fun httpClientContext(): HttpClientContext {
    return clientContext() as? HttpClientContext
        ?: error("Http client context is not available")
}

////////////////////////////////////////

@KtorDsl
context(route: Route)
fun mmrpc(
    block: HttpServerEngine.Builder.() -> Unit,
) {
    applyHttpMmrpcServer(route, block)
}

fun HttpClient.mmrpc(
    block: HttpClientEngine.Builder.() -> Unit = {},
): HttpClientEngine {
    return createHttpMmrpcClient(this, block)
}

////////////////////////////////////////
