package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.Unnamed

////////////////////////////////////////

typealias TraitDefinitionBlock = context(TraitDefinitionBuilder) () -> Unit

@Marker2
class TraitDefinitionBuilder :
    FieldDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    var discriminator: String = "type"
    val fields = mutableListOf<Unnamed<FieldDefinition>>()

    override fun addFieldDefinition(value: Unnamed<FieldDefinition>) {
        fields += value
    }

    fun build(): TraitDefinition {
        val cn = buildCanonicalName()
        return TraitDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            discriminator = this.discriminator,
            fields = this.fields.mapIndexed { i, it ->
                it.get(cn, name = "field$i")
            },
        )
    }
}

context(ctx: TraitDefinitionBuilder)
fun discriminator(value: String) {
    ctx.discriminator = value
}

////////////////////////////////////////

@Marker2
val trait = trait()

@Marker2
fun trait(
    block: TraitDefinitionBlock = {},
) = Unnamed { ns, name ->
    TraitDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

////////////////////////////////////////
