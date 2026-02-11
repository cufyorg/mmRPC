package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG
import org.cufy.mmrpc.internal.asAnonChildOf

////////////////////////////////////////

typealias StructDefinitionBlock = context(StructDefinitionBuilder) () -> Unit

@Marker2
class StructDefinitionBuilder :
    FieldDefinitionContainerBuilder,
    TraitDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val traits = mutableListOf<Unnamed<TraitDefinition>>()
    val fields = mutableListOf<Unnamed<FieldDefinition>>()

    override fun addTraitDefinition(value: Unnamed<TraitDefinition>) {
        traits += value
    }

    override fun addFieldDefinition(value: Unnamed<FieldDefinition>) {
        fields += value
    }

    fun build(): StructDefinition {
        val cn = buildCanonicalName()
        return StructDefinition(
            canonicalName = cn,
            description = buildDescription(),
            metadata = buildMetadata(),
            traits = this.traits.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "trait", i)
            },
            fields = this.fields.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "field", i)
            },
        )
    }
}

////////////////////////////////////////

@Marker2
val struct = struct()

@Marker2
fun struct(
    block: StructDefinitionBlock = {},
) = Unnamed { ns, name ->
    StructDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun struct(
    vararg fields: FieldDefinition,
    block: StructDefinitionBlock = {},
) = struct {
    +fields.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun struct(
    vararg fields: Unnamed<FieldDefinition>,
    block: StructDefinitionBlock = {},
) = struct {
    +fields.asList()
    block()
}

////////////////////////////////////////
