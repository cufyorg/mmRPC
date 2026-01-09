package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG

////////////////////////////////////////

typealias MetadataDefinitionBlock = context(MetadataDefinitionBuilder) () -> Unit

@Marker2
class MetadataDefinitionBuilder :
    FieldDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val fields = mutableListOf<Unnamed<FieldDefinition>>()

    override fun addFieldDefinition(value: Unnamed<FieldDefinition>) {
        fields += value
    }

    fun build(): MetadataDefinition {
        val cn = buildCanonicalName()
        return MetadataDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            fields = this.fields.mapIndexed { i, it ->
                it.get(cn, name = "field$i")
            },
        )
    }
}

////////////////////////////////////////

@Marker2
val metadata = metadata()

@Marker2
fun metadata(
    block: MetadataDefinitionBlock = {},
) = Unnamed { ns, name ->
    MetadataDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun metadata(
    vararg fields: FieldDefinition,
    block: MetadataDefinitionBlock = {},
) = metadata {
    +fields.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun metadata(
    vararg fields: Unnamed<FieldDefinition>,
    block: MetadataDefinitionBlock = {},
) = metadata {
    +fields.asList()
    block()
}

////////////////////////////////////////
