package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("metadata-parameter")
data class CompactMetadataParameterDefinition(
    override val name: String = MetadataParameterDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("parameter_type.ref")
    val parameterType: CanonicalName,
    @SerialName("parameter_default.ref")
    val parameterDefault: CanonicalName? = null,
) : CompactElementDefinition

fun MetadataParameterDefinition.toCompact(): CompactMetadataParameterDefinition {
    return CompactMetadataParameterDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        parameterType = this.parameterType.canonicalName,
        parameterDefault = this.parameterDefault?.canonicalName,
    )
}

fun CompactMetadataParameterDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> MetadataParameterDefinition? {
    return it@{
        MetadataParameterDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            parameterType = this.parameterType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "parameter_type.ref must point to a TypeDefinition"
                }
                item
            },
            parameterDefault = this.parameterDefault?.let {
                val item = onLookup(it) ?: return@it null
                require(item is ConstDefinition) {
                    "parameter_default.ref must point to a ConstDefinition"
                }
                item
            }
        )
    }
}
