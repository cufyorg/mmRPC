package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.asSiblingOf

////////////////////////////////////////

typealias FieldDefinitionBlock = context(FieldDefinitionBuilder) () -> Unit

@Marker2
class FieldDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()
    val default = Box<Literal>()
    var key: String? = null

    fun build(): FieldDefinition {
        val cn = buildCanonicalName()
        return FieldDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            key = this.key,
            type = this::type.getOrThrow()
                .asSiblingOf(cn, suffix = "_type"),
            default = this::default.getOrNull(),
        )
    }
}

context(ctx: FieldDefinitionBuilder)
val default get() = ctx.default

context(ctx: FieldDefinitionBuilder)
fun key(value: String) {
    ctx.key = value
}

////////////////////////////////////////

@Marker2
internal fun prop(
    block: FieldDefinitionBlock = {},
) = Unnamed { ns, name ->
    FieldDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun prop(
    type: TypeDefinition,
    block: FieldDefinitionBlock = {},
) = prop {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
fun prop(
    type: Unnamed<TypeDefinition>,
    block: FieldDefinitionBlock = {},
) = prop {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
fun prop(
    key: String,
    type: TypeDefinition,
    block: FieldDefinitionBlock = {},
) = prop {
    contextOf()
        .also { it.key = key }
        .also { it.type *= type }
    block()
}

@Marker2
fun prop(
    key: String,
    type: Unnamed<TypeDefinition>,
    block: FieldDefinitionBlock = {},
) = prop {
    contextOf()
        .also { it.key = key }
        .also { it.type *= type }
    block()
}

////////////////////////////////////////
