package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("const")
data class CompactConstDefinition(
    override val name: String = ConstDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("const_type.ref")
    val constType: CanonicalName,
    @SerialName("const_value")
    val constValue: String,
) : CompactElementDefinition

fun ConstDefinition.toCompact(): CompactConstDefinition {
    return CompactConstDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        constType = this.constType.canonicalName,
        constValue = this.constValue,
    )
}

fun CompactConstDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> ConstDefinition? {
    return it@{
        ConstDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            constType = this.constType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "const_type.ref must point to a TypeDefinition"
                }
                item
            },
            constValue = this.constValue,
        )
    }
}
