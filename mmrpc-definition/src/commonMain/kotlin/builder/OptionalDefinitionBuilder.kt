package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias OptionalDefinitionBlock = context(OptionalDefinitionBuilder) () -> Unit

@Marker2
class OptionalDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()

    fun build(): OptionalDefinition {
        val cn = buildCanonicalName()
        return OptionalDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this::type.getOrThrow()
                .get(cn, name = "type"),
        )
    }
}

////////////////////////////////////////

@Marker2
internal fun optional(
    block: OptionalDefinitionBlock = {},
) = Unnamed { ns, name ->
    OptionalDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun optional(
    type: TypeDefinition,
    block: OptionalDefinitionBlock = {},
) = optional {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
fun optional(
    type: Unnamed<TypeDefinition>,
    block: OptionalDefinitionBlock = {},
) = optional {
    contextOf()
        .also { it.type *= type }
    block()
}

////////////////////////////////////////

@Marker2
val TypeDefinition.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

@Marker2
val Unnamed<TypeDefinition>.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

////////////////////////////////////////
