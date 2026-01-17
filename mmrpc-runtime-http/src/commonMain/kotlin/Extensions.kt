package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.server.routing.*

context(route: Route)
fun mmRPC(block: HttpServerEngine.() -> Unit) {
    HttpServerEngine(route).block()
}

fun HttpClient.mmRPC(): HttpClientEngine {
    return HttpClientEngine(this)
}
