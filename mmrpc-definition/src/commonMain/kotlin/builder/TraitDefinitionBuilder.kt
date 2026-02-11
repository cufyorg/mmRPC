package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.asAnonChildOf

////////////////////////////////////////

typealias TraitDefinitionBlock = context(TraitDefinitionBuilder) () -> Unit

@Marker2
class TraitDefinitionBuilder :
    FieldDefinitionContainerBuilder,
    TraitDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    var discriminator: String = "type"
    val traits = mutableListOf<Unnamed<TraitDefinition>>()
    val fields = mutableListOf<Unnamed<FieldDefinition>>()

    override fun addTraitDefinition(value: Unnamed<TraitDefinition>) {
        traits += value
    }

    override fun addFieldDefinition(value: Unnamed<FieldDefinition>) {
        fields += value
    }

    fun build(): TraitDefinition {
        val cn = buildCanonicalName()
        return TraitDefinition(
            canonicalName = cn,
            description = buildDescription(),
            metadata = buildMetadata(),
            discriminator = this.discriminator,
            traits = this.traits.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "trait", i)
            },
            fields = this.fields.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "field", i)
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
