package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.asSiblingOf

////////////////////////////////////////

typealias ArrayDefinitionBlock = context(ArrayDefinitionBuilder) () -> Unit

@Marker2
class ArrayDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()

    fun build(): ArrayDefinition {
        val cn = buildCanonicalName()
        return ArrayDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this::type.getOrThrow()
                .asSiblingOf(cn, suffix = "_type"),
        )
    }
}

////////////////////////////////////////

@Marker2
internal fun array(
    block: ArrayDefinitionBlock = {},
) = Unnamed { ns, name ->
    ArrayDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun array(
    type: TypeDefinition,
    block: ArrayDefinitionBlock = {},
) = array {
    contextOf()
        .also { it.type *= type }
    block()
}

@Marker2
fun array(
    type: Unnamed<TypeDefinition>,
    block: ArrayDefinitionBlock = {},
) = array {
    contextOf()
        .also { it.type *= type }
    block()
}

////////////////////////////////////////

@Marker2
val TypeDefinition.array: Unnamed<ArrayDefinition>
    get() = array(this)

@Marker2
val Unnamed<TypeDefinition>.array: Unnamed<ArrayDefinition>
    get() = array(this)

////////////////////////////////////////
