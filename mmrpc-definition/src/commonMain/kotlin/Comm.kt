package org.cufy.mmrpc

import kotlinx.serialization.Serializable

@Serializable
enum class Comm(val input: Shape, val output: Shape) {
    /** Event broadcasted from service to client. */
    VoidUnary(Shape.Void, Shape.Unary),
    /** Event emitted from client to service. */
    UnaryVoid(Shape.Unary, Shape.Void),
    /** Traditional single Request-Response */
    UnaryUnary(Shape.Unary, Shape.Unary),
    /** Stream requests finished by a single response */
    StreamUnary(Shape.Stream, Shape.Unary),
    /** Stream responses started by a single request */
    UnaryStream(Shape.Unary, Shape.Stream),
    /** Bidi Streaming */
    StreamStream(Shape.Stream, Shape.Stream);

    enum class Shape {
        Void,
        Unary,
        Stream;
    }

    companion object {
        fun of(input: Shape, output: Shape) = when (input) {
            Shape.Void -> when (output) {
                Shape.Void -> null
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
