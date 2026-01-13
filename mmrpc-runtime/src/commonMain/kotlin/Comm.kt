package org.cufy.mmrpc

import kotlinx.serialization.Serializable

@Serializable
enum class Comm(val input: Shape, val output: Shape) {
    VoidVoid(Shape.Void, Shape.Void),
    VoidUnary(Shape.Void, Shape.Unary),
    UnaryVoid(Shape.Unary, Shape.Void),
    UnaryUnary(Shape.Unary, Shape.Unary),
    UnaryStream(Shape.Unary, Shape.Stream),
    StreamUnary(Shape.Stream, Shape.Unary),
    StreamStream(Shape.Stream, Shape.Stream);

    enum class Shape {
        Void,
        Unary,
        Stream;
    }

    companion object {
        fun of(input: Shape, output: Shape) = when (input) {
            Shape.Void -> when (output) {
                Shape.Void -> VoidVoid
                Shape.Unary -> VoidUnary
                Shape.Stream -> null
            }

            Shape.Unary -> when (output) {
                Shape.Void -> UnaryVoid
                Shape.Unary -> UnaryUnary
                Shape.Stream -> UnaryStream
            }

            Shape.Stream -> when (output) {
                Shape.Unary -> StreamUnary
                Shape.Stream -> StreamStream
                Shape.Void -> null
            }
        }
    }
}
