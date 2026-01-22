package org.cufy.mmrpc.runtime.grpc.internal

import io.grpc.MethodDescriptor

internal fun MethodDescriptor.Builder<*, *>.setCanonicalName(canonicalName: String) {
    val di = canonicalName.lastIndexOf('.')
    val sn = canonicalName.substring(0, di)
    val mn = canonicalName.substring(di + 1)
    setFullMethodName("$sn/$mn")
}

internal fun MethodDescriptor<*, *>.canonicalName(): String {
    val di = fullMethodName.lastIndexOf('/')
    val sn = fullMethodName.substring(0, di)
    val mn = fullMethodName.substring(di + 1)
    return "$sn.$mn"
}
