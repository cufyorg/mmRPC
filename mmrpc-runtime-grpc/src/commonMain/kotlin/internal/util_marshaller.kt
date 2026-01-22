package org.cufy.mmrpc.runtime.grpc.internal

import io.grpc.MethodDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
internal fun <T> KSerializer<T>.marshaller(): MethodDescriptor.Marshaller<T> {
    return object : MethodDescriptor.Marshaller<T> {
        override fun stream(value: T): InputStream =
            ProtoBuf.encodeToByteArray(this@marshaller, value).inputStream()

        override fun parse(stream: InputStream): T =
            ProtoBuf.decodeFromByteArray(this@marshaller, stream.readAllBytes())
    }
}
