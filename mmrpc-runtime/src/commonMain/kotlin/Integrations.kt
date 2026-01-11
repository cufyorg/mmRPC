package org.cufy.mmrpc

import org.cufy.mmrpc.Comm.*

fun Comm.isGrpcSupported() = true
fun Comm.isKrpcSupported() = true
fun Comm.isHttpSupported() = when (this) {
    VoidVoid,
    UnaryVoid,
    UnaryUnary,
    -> true

    else -> false
}

fun Comm.isKafkaSupported() = when (this) {
    VoidVoid,
    UnaryVoid,
    VoidUnary,
    -> true

    else -> false
}
