package org.cufy.specdsl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

////////////////////////////////////////

@Serializable
@SerialName("metadata-parameter")
data class MetadataParameterDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("parameter_type")
    val parameterType: ScalarDefinition,
    @SerialName("parameter_is_optional")
    val parameterIsOptional: Boolean = false,
    @SerialName("parameter_default")
    val parameterDefault: ConstDefinition? = null,
) : ElementDefinition {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<metadata-parameter>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(parameterType.collect())
        parameterDefault?.let { yieldAll(it.collect()) }
    }
}

open class MetadataParameterDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = MetadataParameterDefinition.ANONYMOUS_NAME

    open var isOptional = false

    open val type = DomainProperty<ScalarDefinition>()
    open val default = OptionalDomainProperty<ConstDefinition>()

    override fun build(): MetadataParameterDefinition {
        val asNamespace = this.namespace.value + this.name
        return MetadataParameterDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            parameterIsOptional = this.isOptional,
            parameterType = this.type.value.get(asNamespace),
            parameterDefault = this.default.value?.get(asNamespace),
        )
    }
}

@Marker1
@Suppress("FunctionName")
internal fun metadata_param(
    block: MetadataParameterDefinitionBuilder.() -> Unit = {},
): Unnamed<MetadataParameterDefinition> {
    return Unnamed { namespace, name ->
        MetadataParameterDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
@Suppress("FunctionName")
fun metadata_param(
    type: ScalarDefinition,
    block: MetadataParameterDefinitionBuilder.() -> Unit = {},
): Unnamed<MetadataParameterDefinition> {
    return metadata_param { this.type *= type; block() }
}

@Marker1
@Suppress("FunctionName")
fun metadata_param(
    type: Unnamed<ScalarDefinition>,
    block: MetadataParameterDefinitionBuilder.() -> Unit = {},
): Unnamed<MetadataParameterDefinition> {
    return metadata_param { this.type *= type; block() }
}

////////////////////////////////////////
