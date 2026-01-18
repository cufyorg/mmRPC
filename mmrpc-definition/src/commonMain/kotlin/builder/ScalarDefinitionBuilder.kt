package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.asAnonSiblingOf

////////////////////////////////////////

typealias ScalarDefinitionBlock = context(ScalarDefinitionBuilder) () -> Unit

@Marker2
class ScalarDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<ScalarDefinition>>()

    fun build(): ScalarDefinition {
        val cn = buildCanonicalName()
        return ScalarDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this::type.getOrNull()
                ?.asAnonSiblingOf(cn, "type"),
        )
    }
}

context(ctx: ScalarDefinitionBuilder)
val type get() = ctx.type

////////////////////////////////////////

@Marker2
val scalar = scalar()

@Marker2
fun scalar(
    block: ScalarDefinitionBlock = {},
) = Unnamed { ns, name ->
    ScalarDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .also(block)
        .build()
}

@Marker2
fun scalar(
    type: ScalarDefinition,
    block: ScalarDefinitionBlock = {},
) = scalar {
    contextOf()
        .also { it.type *= type }
    block()
}

////////////////////////////////////////
