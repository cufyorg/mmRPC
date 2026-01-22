package org.cufy.mmrpc.runtime.grpc.internal

import io.grpc.*
import io.grpc.kotlin.CoroutineContextServerInterceptor
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.grpc.GrpcServerContext
import org.cufy.mmrpc.runtime.grpc.GrpcServerEngine
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMmrpcApi::class)
internal fun applyGrpcMmrpcServer(
    server: ServerBuilder<*>,
    context: CoroutineContext,
    block: GrpcServerEngine.Builder.() -> Unit,
) {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : GrpcServerEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Server>()
        val routes = mutableListOf<context(GrpcServerEngine) () -> Unit>()

        override fun install(interceptor: Interceptor.Server) {
            interceptors.add(interceptor)
        }

        override fun routing(block: context(GrpcServerEngine) () -> Unit) {
            routes.add(block)
        }
    }

    builder.apply(block)

    val methods = mutableListOf<ServerMethodDefinition<*, *>>()
    val engine = GrpcServerEngine(
        coroutineContext = context,
        interceptors = builder.interceptors,
        register = { methods += it }
    )

    builder.routes.forEach {
        engine.apply(it)
    }

    methods
        .groupBy { it.methodDescriptor.serviceName }
        .forEach { (serviceName, methods) ->
            ServerServiceDefinition.builder(serviceName)
                .apply { methods.forEach { addMethod(it) } }
                .also { server.addService(it.build()) }
        }

    server.intercept(object : CoroutineContextServerInterceptor() {
        override fun coroutineContext(
            call: ServerCall<*, *>,
            headers: Metadata,
        ): CoroutineContext {
            val canonicalName = call.methodDescriptor.canonicalName()
            return GrpcServerContext(call, headers, canonicalName)
        }
    })
}
