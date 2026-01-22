package org.cufy.mmrpc.runtime.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

typealias WrapHandler0<Req> = suspend context(CoroutineScope) (Req) -> Unit
typealias WrapHandler1<Req, Res> = suspend context(CoroutineScope) (Req) -> Res
typealias WrapHandler2<Req, Res> = suspend context(CoroutineScope) (Flow<Req>) -> Res
typealias WrapHandler3<Req, Res> = (Req) -> Flow<Res>
typealias WrapHandler4<Req, Res> = (Flow<Req>) -> Flow<Res>

inline fun <Req : Any> wrap0(crossinline block: WrapHandler0<Req>): Handler0<Req> =
    { req -> coroutineScope { block(this, req) } }

inline fun <Req : Any, Res : Any> wrap1(crossinline block: WrapHandler1<Req, Res>): Handler1<Req, Res> =
    { req -> coroutineScope { block(this, req) } }

inline fun <Req : Any, Res : Any> wrap2(crossinline block: WrapHandler2<Req, Res>): Handler2<Req, Res> =
    { req -> coroutineScope { block(this, req) } }

fun <Req : Any, Res : Any> wrap3(block: WrapHandler3<Req, Res>): Handler3<Req, Res> =
    block

fun <Req : Any, Res : Any> wrap4(block: WrapHandler4<Req, Res>): Handler4<Req, Res> =
    block
