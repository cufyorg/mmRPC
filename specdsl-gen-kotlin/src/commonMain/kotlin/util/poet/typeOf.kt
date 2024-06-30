package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName

/*
typeOf:

returns the assignable type of some element.
*/

@Marker0
fun GenGroup.typeOf(element: TypeDefinition): TypeName {
    if (element is ConstDefinition)
        return typeOf(element.constType)

    if (element is OptionalDefinition)
        return typeOf(element.optionalType).copy(nullable = true)

    if (element is ArrayDefinition)
        return LIST.parameterizedBy(typeOf(element.arrayType))

    if (element.isAnonymous)
        return ANY.copy(nullable = true)

    if (element is ScalarDefinition) {
        if (element.canonicalName in ctx.nativeElements)
            return nativeClassOf(element)

        return ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
    }

    return when (element) {
        is UnionDefinition -> ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
        is InterDefinition -> ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
        is StructDefinition -> ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)
        is TupleDefinition -> ClassName(ctx.pkg, element.namespace.asClassName, element.asClassName)

        else -> error("unexpected state") // idk why the compiler is complaining here!
    }
}
