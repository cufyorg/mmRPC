package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

@KtorDsl
@OptIn(ExperimentalMmrpcApi::class)
context(route: Route)
fun mmrpc(block: HttpServerEngine.Builder.() -> Unit) {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : HttpServerEngine.Builder {
        val interceptors = mutableListOf<HttpServerInterceptor>()
        var contentNegotiator: HttpServerContentNegotiator? = null
        val routes = mutableListOf<context(HttpServerEngine) () -> Unit>()

        override fun install(interceptor: HttpServerInterceptor) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: HttpServerContentNegotiator) {
            check(contentNegotiator == null) { "Content negotiator already installed" }
            contentNegotiator = negotiator
        }

        override fun routing(block: context(HttpServerEngine) () -> Unit) {
            routes.add(block)
        }
    }

    builder.apply(block)

    val engine = HttpServerEngine(
        route = route,
        contentNegotiator = builder.contentNegotiator
            ?: HttpServerContentNegotiator.Default,
        interceptors = builder.interceptors,
    )

    builder.routes.forEach {
        engine.apply(it)
    }
}

@OptIn(ExperimentalMmrpcApi::class)
fun HttpClient.mmrpc(block: HttpClientEngine.Builder.() -> Unit = {}): HttpClientEngine {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : HttpClientEngine.Builder {
        val interceptors = mutableListOf<HttpClientInterceptor>()
        var contentNegotiator: HttpClientContentNegotiator? = null

        override fun install(interceptor: HttpClientInterceptor) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: HttpClientContentNegotiator) {
            check(contentNegotiator == null) { "Content negotiator already installed" }
            contentNegotiator = negotiator
        }
    }

    builder.apply(block)

    val engine = HttpClientEngine(
        client = this,
        contentNegotiator = builder.contentNegotiator
            ?: HttpClientContentNegotiator.Default,
        interceptors = builder.interceptors,
    )

    return engine
}
