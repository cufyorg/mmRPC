package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG

////////////////////////////////////////

typealias StructDefinitionBlock = context(StructDefinitionBuilder) () -> Unit

@Marker2
class StructDefinitionBuilder :
    FieldDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val fields = mutableListOf<Unnamed<FieldDefinition>>()

    override fun addFieldDefinition(value: Unnamed<FieldDefinition>) {
        fields += value
    }

    fun build(): StructDefinition {
        val cn = buildCanonicalName()
        return StructDefinition(
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
