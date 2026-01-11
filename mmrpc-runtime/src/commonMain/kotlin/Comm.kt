package org.cufy.mmrpc

import kotlinx.serialization.Serializable

@Serializable
enum class Comm {
    VoidVoid,
    VoidUnary,
    UnaryVoid,
    UnaryUnary,
    UnaryStream,
    StreamUnary,
    StreamStream;

    enum class Shape {
        Void,
        Unary,
        Stream;
    }

    fun inputShape() = when (this) {
        VoidVoid,
        VoidUnary,
        -> Shape.Void

        UnaryVoid,
        UnaryUnary,
        UnaryStream,
        -> Shape.Unary

        StreamUnary,
        StreamStream,
        -> Shape.Stream
    }

    fun outputShape() = when (this) {
        VoidVoid,
        UnaryVoid,
        -> Shape.Void

        VoidUnary,
        UnaryUnary,
        StreamUnary,
        -> Shape.Unary

        UnaryStream,
        StreamStream,
        -> Shape.Stream
    }

    companion object {
        fun of(req: Shape, res: Shape) = when (req) {
            Shape.Void -> when (res) {
                Shape.Void -> VoidVoid
                Shape.Unary -> VoidUnary
                else -> null
            }

            Shape.Unary -> when (res) {
                Shape.Void -> UnaryVoid
                Shape.Unary -> UnaryUnary
                Shape.Stream -> UnaryStream
            }

            Shape.Stream -> when (res) {
                Shape.Unary -> StreamUnary
                Shape.Stream -> StreamStream
                else -> null
            }
        }
    }
}
