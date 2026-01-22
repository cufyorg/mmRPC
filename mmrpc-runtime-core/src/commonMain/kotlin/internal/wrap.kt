package org.cufy.mmrpc.runtime.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

typealias Wrap0<Req> = suspend context(CoroutineScope) (Req) -> Unit
typealias Wrap1<Req, Res> = suspend context(CoroutineScope) (Req) -> Res
typealias Wrap2<Req, Res> = suspend context(CoroutineScope) (Flow<Req>) -> Res
typealias Wrap3<Req, Res> = (Req) -> Flow<Res>
typealias Wrap4<Req, Res> = (Flow<Req>) -> Flow<Res>

inline fun <Req : Any> wrap0(crossinline block: Wrap0<Req>): Register0<Req> =
    { req -> coroutineScope { block(this, req) } }

inline fun <Req : Any, Res : Any> wrap1(crossinline block: Wrap1<Req, Res>): Register1<Req, Res> =
    { req -> coroutineScope { block(this, req) } }

inline fun <Req : Any, Res : Any> wrap2(crossinline block: Wrap2<Req, Res>): Register2<Req, Res> =
    { req -> coroutineScope { block(this, req) } }

fun <Req : Any, Res : Any> wrap3(block: Wrap3<Req, Res>): Register3<Req, Res> =
    block

fun <Req : Any, Res : Any> wrap4(block: Wrap4<Req, Res>): Register4<Req, Res> =
    block
