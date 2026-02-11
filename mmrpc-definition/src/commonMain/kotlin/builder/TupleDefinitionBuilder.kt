package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.TypeDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG
import org.cufy.mmrpc.internal.asAnonSiblingOf

////////////////////////////////////////

typealias TupleDefinitionBlock = context(TupleDefinitionBuilder) () -> Unit

@Marker2
class TupleDefinitionBuilder :
    TypeDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val types = mutableListOf<Unnamed<TypeDefinition>>()

    override fun addTypeDefinition(value: Unnamed<TypeDefinition>) {
        types += value
    }

    fun build(): TupleDefinition {
        val cn = buildCanonicalName()
        return TupleDefinition(
            canonicalName = cn,
            description = buildDescription(),
            metadata = buildMetadata(),
            types = this.types.mapIndexed { i, it ->
                it.asAnonSiblingOf(cn, "type", i)
            },
        )
    }
}

////////////////////////////////////////

@Marker2
val tuple = tuple()

@Marker2
fun tuple(
    block: TupleDefinitionBlock = {},
) = Unnamed { ns, name ->
    TupleDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun tuple(
    vararg types: TypeDefinition,
    block: TupleDefinitionBlock = {},
) = tuple {
    +types.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun tuple(
    vararg types: Unnamed<TypeDefinition>,
    block: TupleDefinitionBlock = {},
) = tuple {
    +types.asList()
    block()
}

////////////////////////////////////////
