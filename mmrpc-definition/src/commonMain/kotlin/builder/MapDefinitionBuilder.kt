package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.asAnonSiblingOf

////////////////////////////////////////

typealias MapDefinitionBlock = context(MapDefinitionBuilder) () -> Unit

@Marker2
class MapDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()

    fun build(): MapDefinition {
        val cn = buildCanonicalName()
        return MapDefinition(
            canonicalName = cn,
            description = buildDescription(),
            metadata = buildMetadata(),
            type = this::type.getOrThrow()
                .asAnonSiblingOf(cn, "type"),
        )
    }
}

////////////////////////////////////////

@Marker2
internal fun map(
    block: MapDefinitionBlock = {},
) = Unnamed { ns, name ->
    MapDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun map(
    type: TypeDefinition,
    block: MapDefinitionBlock = {},
) = map {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
fun map(
    type: Unnamed<TypeDefinition>,
    block: MapDefinitionBlock = {},
) = map {
    contextOf()
        .also { it.type *= type }
    block()
}

////////////////////////////////////////
