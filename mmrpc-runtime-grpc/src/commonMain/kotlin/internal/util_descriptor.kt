package org.cufy.mmrpc.runtime.grpc.internal

import com.google.protobuf.Empty
import io.grpc.MethodDescriptor
import io.grpc.protobuf.ProtoUtils
import kotlinx.serialization.KSerializer

internal fun <Req : Any> descriptor0(
    canonicalName: String,
    reqSerial: KSerializer<Req>,
): MethodDescriptor<Req, Empty> {
    return MethodDescriptor.newBuilder<Req, Empty>()
        .apply {
            setType(MethodDescriptor.MethodType.UNARY)
            setCanonicalName(canonicalName)
            setRequestMarshaller(reqSerial.marshaller())
            setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
        }
        .build()
}

internal fun <Req : Any, Res : Any> descriptor1(
    canonicalName: String,
    reqSerial: KSerializer<Req>,
    resSerial: KSerializer<Res>,
): MethodDescriptor<Req, Res> {
    return MethodDescriptor.newBuilder<Req, Res>()
        .apply {
            setType(MethodDescriptor.MethodType.UNARY)
            setCanonicalName(canonicalName)
            setRequestMarshaller(reqSerial.marshaller())
            setResponseMarshaller(resSerial.marshaller())
        }
        .build()
}

internal fun <Req : Any, Res : Any> descriptor2(
    canonicalName: String,
    reqSerial: KSerializer<Req>,
    resSerial: KSerializer<Res>,
): MethodDescriptor<Req, Res> {
    return MethodDescriptor.newBuilder<Req, Res>()
        .apply {
            setType(MethodDescriptor.MethodType.CLIENT_STREAMING)
            setCanonicalName(canonicalName)
            setRequestMarshaller(reqSerial.marshaller())
            setResponseMarshaller(resSerial.marshaller())
        }
        .build()
}

internal fun <Req : Any, Res : Any> descriptor3(
    canonicalName: String,
    reqSerial: KSerializer<Req>,
    resSerial: KSerializer<Res>,
): MethodDescriptor<Req, Res> {
    return MethodDescriptor.newBuilder<Req, Res>()
        .apply {
            setType(MethodDescriptor.MethodType.SERVER_STREAMING)
            setCanonicalName(canonicalName)
            setRequestMarshaller(reqSerial.marshaller())
            setResponseMarshaller(resSerial.marshaller())
        }
        .build()
}

internal fun <Req : Any, Res : Any> descriptor4(
    canonicalName: String,
    reqSerial: KSerializer<Req>,
    resSerial: KSerializer<Res>,
): MethodDescriptor<Req, Res> {
    return MethodDescriptor.newBuilder<Req, Res>()
        .apply {
            setType(MethodDescriptor.MethodType.BIDI_STREAMING)
            setCanonicalName(canonicalName)
            setRequestMarshaller(reqSerial.marshaller())
            setResponseMarshaller(resSerial.marshaller())
        }
        .build()
}
