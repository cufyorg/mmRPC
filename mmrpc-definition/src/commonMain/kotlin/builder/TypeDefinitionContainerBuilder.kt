package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TypeDefinition
import org.cufy.mmrpc.Unnamed
import kotlin.jvm.JvmName

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

@JvmName("Iterable_Unnamed_TypeDefinition_unaryPlus")
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
