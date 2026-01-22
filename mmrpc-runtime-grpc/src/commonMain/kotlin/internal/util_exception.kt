package org.cufy.mmrpc.runtime.grpc.internal

import com.google.protobuf.Any
import com.google.rpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import org.cufy.mmrpc.runtime.FaultObject

internal fun FaultObject.toStatusRuntimeException(): StatusRuntimeException {
    val status = Status.newBuilder()
        .setMessage(this.message.orEmpty())
        .addDetails(
            Any.newBuilder()
                .setTypeUrl(this.canonicalName)
                .build()
        )
        .build()
    return StatusProto.toStatusRuntimeException(status)
}

internal fun StatusRuntimeException.toFaultObjectOrNull(): FaultObject? {
    val status = StatusProto.fromThrowable(this) ?: return null
    val detail = status.detailsList.firstOrNull() ?: return null
    return FaultObject(detail.typeUrl, status.message)
}
