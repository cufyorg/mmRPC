package org.cufy.mmrpc.builder

import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG
import org.cufy.mmrpc.internal.asSiblingOf

////////////////////////////////////////

typealias InterDefinitionBlock = context(InterDefinitionBuilder) () -> Unit

@Marker2
class InterDefinitionBuilder :
    StructDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val types = mutableListOf<Unnamed<StructDefinition>>()

    override fun addStructDefinition(value: Unnamed<StructDefinition>) {
        types += value
    }

    fun build(): InterDefinition {
        val cn = buildCanonicalName()
        return InterDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            types = this.types.mapIndexed { i, it ->
                it.asSiblingOf(cn, suffix = "_type$i")
            },
        )
    }
}

////////////////////////////////////////

@Marker2
fun inter(
    block: InterDefinitionBlock = {},
) = Unnamed { ns, name ->
    InterDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun inter(
    vararg types: StructDefinition,
    block: InterDefinitionBlock = {},
) = inter {
    +types.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun inter(
    vararg types: Unnamed<StructDefinition>,
    block: InterDefinitionBlock = {},
) = inter {
    +types.asList()
    block()
}

////////////////////////////////////////
