package org.cufy.mmrpc.runtime.grpc.internal

import io.grpc.ManagedChannel
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.grpc.GrpcClientEngine

@OptIn(ExperimentalMmrpcApi::class)
internal fun createGrpcMmrpcClient(
    channel: ManagedChannel,
    block: GrpcClientEngine.Builder.() -> Unit = {},
): GrpcClientEngine {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : GrpcClientEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Client>()

        override fun install(interceptor: Interceptor.Client) {
            interceptors.add(interceptor)
        }
    }

    builder.apply(block)

    val engine = GrpcClientEngine(
        channel = channel,
        interceptors = builder.interceptors,
    )

    return engine
}
