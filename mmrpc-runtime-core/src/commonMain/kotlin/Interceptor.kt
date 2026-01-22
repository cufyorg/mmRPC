package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow

interface Interceptor {
    interface Server : Interceptor
    interface Client : Interceptor

    companion object {
        @ExperimentalMmrpcApi
        suspend fun <Req> foldRequest(
            interceptors: List<Interceptor>,
            canonicalName: String,
            request: Req,
        ): Req = interceptors.fold(request) { acc, next ->
            next.onRequest(canonicalName, acc)
        }

        @ExperimentalMmrpcApi
        suspend fun <Req> foldRequest(
            interceptors: List<Interceptor>,
            canonicalName: String,
            stream: Flow<Req>,
        ): Flow<Req> = interceptors.fold(stream) { acc, next ->
            next.onRequest(canonicalName, acc)
        }

        @ExperimentalMmrpcApi
        suspend fun <Res> foldResponse(
            interceptors: List<Interceptor>,
            canonicalName: String,
            stream: Res,
        ): Res = interceptors.fold(stream) { acc, next ->
            next.onResponse(canonicalName, acc)
        }

        @ExperimentalMmrpcApi
        suspend fun <Res> foldResponse(
            interceptors: List<Interceptor>,
            canonicalName: String,
            response: Flow<Res>
        ): Flow<Res> = interceptors.fold(response) { acc, next ->
            next.onResponse(canonicalName, acc)
        }

        @ExperimentalMmrpcApi
        suspend fun foldError(
            interceptors: List<Interceptor>,
            canonicalName: String,
            error: FaultObject,
        ): FaultObject = interceptors.fold(error) { acc, next ->
            next.onError(canonicalName, acc)
        }
    }

    /**
     * Intercepts and processes a request.
     *
     * @param canonicalName The canonical name of the routine.
     * @param request The incoming request to be processed.
     * @return The processed request after interception.
     */
    @ExperimentalMmrpcApi
    suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    /**
     * Intercepts and processes a stream of requests.
     *
     * @param canonicalName The canonical name of the routine.
     * @param stream The incoming stream of requests to be processed.
     * @return The processed stream of requests after interception.
     */
    @ExperimentalMmrpcApi
    suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> = stream

    /**
     * Intercepts and processes a response.
     *
     * @param canonicalName The canonical name of the routine.
     * @param response The outgoing response to be processed.
     * @return The processed response after interception.
     */
    @ExperimentalMmrpcApi
    suspend fun <Res> onResponse(canonicalName: String, response: Res): Res = response

    /**
     * Intercepts and processes a stream.
     *
     * @param canonicalName The canonical name of the routine.
     * @param stream The outgoing stream of responses to be processed.
     * @return The processed stream of responses after interception.
     */
    @ExperimentalMmrpcApi
    suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> = stream

    /**
     * Intercepts and processes an error.
     *
     * @param canonicalName The canonical name of the routine.
     * @param error The fault object representing the error details.
     * @return The processed or modified fault object after interception.
     */
    @ExperimentalMmrpcApi
    suspend fun onError(canonicalName: String, error: FaultObject): FaultObject = error
}
