package org.cufy.mmrpc

import org.cufy.mmrpc.Comm.*

fun Comm.isGrpcSupported() = when (this) {
    UnaryVoid, // Regular rpc call without awaiting response
    UnaryUnary, // Regular rpc call
    UnaryStream, // rpc with single input and stream output
    StreamUnary, // rpc with stream input and single output
    StreamStream, // rpc with stream input and stream output
    -> true

    VoidUnary,
    -> false
}

fun Comm.isKrpcSupported() = when (this) {
    UnaryVoid, // Regular rpc call without awaiting response
    UnaryUnary, // Regular rpc call
    UnaryStream, // rpc with single input and flow output
    StreamUnary, // rpc with flow input and single output
    StreamStream, // rpc with flow input and flow output
    -> true

    VoidUnary,
    -> false
}

fun Comm.isHttpSupported() = when (this) {
    UnaryVoid, // Regular HTTP call without awaiting response
    UnaryUnary, // Regular HTTP call
    -> true

    VoidUnary,
    UnaryStream,
    StreamUnary,
    StreamStream,
    -> false
}

fun Comm.isKafkaSupported() = when (this) {
    UnaryVoid, // Client emits to topic
    VoidUnary, // service emits to topic
    -> true

    UnaryUnary,
    UnaryStream,
    StreamUnary,
    StreamStream,
    -> false
}
