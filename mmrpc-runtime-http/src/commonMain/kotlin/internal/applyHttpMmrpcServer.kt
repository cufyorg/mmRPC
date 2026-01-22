package org.cufy.mmrpc.runtime.http.internal

import io.ktor.server.routing.*
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.http.HttpServerEngine
import org.cufy.mmrpc.runtime.http.util.HttpServerNegotiator

@OptIn(ExperimentalMmrpcApi::class)
internal fun applyHttpMmrpcServer(
    route: Route,
    block: HttpServerEngine.Builder.() -> Unit,
) {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : HttpServerEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Server>()
        var negotiator: HttpServerNegotiator? = null
        val routes = mutableListOf<context(HttpServerEngine) () -> Unit>()

        override fun install(interceptor: Interceptor.Server) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: HttpServerNegotiator) {
            check(this.negotiator == null) { "Negotiator already installed" }
            this.negotiator = negotiator
        }

        override fun routing(block: context(HttpServerEngine) () -> Unit) {
            routes.add(block)
        }
    }

    builder.apply(block)

    val engine = HttpServerEngine(
        route = route,
        negotiator = builder.negotiator
            ?: HttpServerNegotiator.Default,
        interceptors = builder.interceptors,
    )

    builder.routes.forEach {
        engine.apply(it)
    }
}
