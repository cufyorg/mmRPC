package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TypeDefinition
import org.cufy.mmrpc.Unnamed

////////////////////////////////////////

@Marker2
interface TypeDefinitionContainerBuilder {
    fun addTypeDefinition(value: Unnamed<TypeDefinition>)
}

////////////////////////////////////////

context(ctx: TypeDefinitionContainerBuilder)
operator fun Unnamed<TypeDefinition>.unaryPlus() {
    ctx.addTypeDefinition(this)
}

context(ctx: TypeDefinitionContainerBuilder)
operator fun Iterable<Unnamed<TypeDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: TypeDefinitionContainerBuilder)
operator fun TypeDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: TypeDefinitionContainerBuilder)
operator fun Iterable<TypeDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////
