package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG
import org.cufy.mmrpc.internal.asAnonChildOf

////////////////////////////////////////

typealias EnumDefinitionBlock = context(EnumDefinitionBuilder) () -> Unit

@Marker2
open class EnumDefinitionBuilder :
    ConstDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()
    val entries = mutableListOf<Unnamed<ConstDefinition>>()

    override fun addConstDefinition(value: Unnamed<ConstDefinition>) {
        entries += value
    }

    fun build(): EnumDefinition {
        val cn = buildCanonicalName()
        return EnumDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this::type.getOrThrow()
                .asAnonChildOf(cn, "type"),
            entries = this.entries.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "entry", i)
            },
        )
    }
}

////////////////////////////////////////

@Marker2
internal fun enum(
    block: EnumDefinitionBlock = {},
) = Unnamed { ns, name ->
    EnumDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun enum(
    type: TypeDefinition,
    block: EnumDefinitionBlock = {},
) = enum {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun enum(
    type: TypeDefinition,
    vararg entries: ConstDefinition,
    block: EnumDefinitionBlock = {},
) = enum {
    contextOf()
        .also { it.type *= type }
    +entries.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun enum(
    type: TypeDefinition,
    vararg entries: Unnamed<ConstDefinition>,
    block: EnumDefinitionBlock = {},
) = enum {
    contextOf()
        .also { it.type *= type }
    +entries.asList()
    block()
}

////////////////////////////////////////
