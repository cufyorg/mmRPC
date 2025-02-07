package org.cufy.mmrpc

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.compact.CompactElementDefinition

@Serializable
data class MmrpcSpec(
    val name: String,
    val version: String,
    val sections: List<CanonicalName>,
    val elements: List<CompactElementDefinition>,
)
