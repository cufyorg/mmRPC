package org.cufy.mmrpc.experimental

import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.Comm.*

fun Comm.isSxSupported() = when (this) {
    UnaryVoid, // Event emission
    VoidUnary, // Event emission (reflux)
    -> true

    UnaryUnary,
    UnaryStream,
    StreamUnary,
    StreamStream,
    -> false
}

fun Comm.isHdxSupported() = when (this) {
    UnaryVoid, // Regular call without awaiting response
    UnaryUnary, // Regular call
    -> true

    VoidUnary,
    UnaryStream,
    StreamUnary,
    StreamStream,
    -> false
}

fun Comm.isFdxSupported() = when (this) {
    UnaryVoid, // Regular call without awaiting response
    UnaryUnary, // Regular call
    UnaryStream, // single input and streaming output
    StreamUnary, // streaming input and single output
    StreamStream, // streaming input and streaming output
    -> true

    VoidUnary,
    -> false
}
