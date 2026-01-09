package org.cufy.mmrpc

import kotlin.reflect.KProperty0

////////////////////////////////////////

class Box<T : Any> {
    var value: T? = null
}

fun <T : Any> KProperty0<Box<T>>.getOrNull(): T? =
    this.get().value

fun <T : Any> KProperty0<Box<T>>.getOrThrow(): T =
    this.get().value ?: error("Required value `${this.name}` was not set")

operator fun <T : Any> Box<T>.timesAssign(value: T) =
    run { this.value = value }

operator fun <T> Box<Unnamed<T>>.timesAssign(value: T) =
    run { this.value = Unnamed(value) }

operator fun Box<Literal>.timesAssign(value: ConstDefinition) =
    run { this.value = value.value }
