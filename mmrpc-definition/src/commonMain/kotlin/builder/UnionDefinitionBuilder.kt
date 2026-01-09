package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG

////////////////////////////////////////

typealias UnionDefinitionBlock = context(UnionDefinitionBuilder) () -> Unit

@Marker2
class UnionDefinitionBuilder :
    StructDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    var discriminator: String = "type"
    val types = mutableListOf<Unnamed<StructDefinition>>()

    override fun addStructDefinition(value: Unnamed<StructDefinition>) {
        types += value
    }

    fun build(): UnionDefinition {
        val cn = buildCanonicalName()
        return UnionDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            discriminator = this.discriminator,
            types = this.types.mapIndexed { i, it ->
                it.get(cn, name = "type$i")
            },
        )
    }
}

////////////////////////////////////////

@Marker2
fun union(
    block: UnionDefinitionBlock = {},
) = Unnamed { ns, name ->
    UnionDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun union(
    vararg types: StructDefinition,
    block: UnionDefinitionBlock = {},
) = union {
    +types.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun union(
    vararg types: Unnamed<StructDefinition>,
    block: UnionDefinitionBlock = {},
) = union {
    +types.asList()
    block()
}

////////////////////////////////////////
