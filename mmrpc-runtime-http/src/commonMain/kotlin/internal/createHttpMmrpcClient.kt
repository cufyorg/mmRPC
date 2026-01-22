package org.cufy.mmrpc.runtime.http.internal

import io.ktor.client.*
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.http.HttpClientEngine
import org.cufy.mmrpc.runtime.http.util.HttpClientNegotiator

@OptIn(ExperimentalMmrpcApi::class)
internal fun createHttpMmrpcClient(
    client: HttpClient,
    block: HttpClientEngine.Builder.() -> Unit = {},
): HttpClientEngine {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : HttpClientEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Client>()
        var negotiator: HttpClientNegotiator? = null

        override fun install(interceptor: Interceptor.Client) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: HttpClientNegotiator) {
            check(this.negotiator == null) { "Negotiator already installed" }
            this.negotiator = negotiator
        }
    }

    builder.apply(block)

    val engine = HttpClientEngine(
        client = client,
        negotiator = builder.negotiator
            ?: HttpClientNegotiator.Default,
        interceptors = builder.interceptors,
    )

    return engine
}
