package org.cufy.mmrpc.runtime.grpc.internal

import com.google.protobuf.Any
import com.google.rpc.Status
import io.grpc.Status.Code
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import org.cufy.mmrpc.runtime.FaultObject

internal fun FaultObject.toStatusRuntimeException(): StatusRuntimeException {
    val status = Status.newBuilder()
        .setCode(Code.UNKNOWN.value())
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
    if (status.code != Code.UNKNOWN.value()) return null
    val detail = status.detailsList.singleOrNull() ?: return null
    return FaultObject(detail.typeUrl, status.message)
}
