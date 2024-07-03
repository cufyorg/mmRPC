package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

/**
 * Returns the assignable type of some element.
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

    return when (element) {
        is ScalarDefinition -> classOf(element)
        is UnionDefinition -> classOf(element)
        is StructDefinition -> classOf(element)

        is InterDefinition,
        is TupleDefinition,
        -> ANY.copy(nullable = true)

        else
        -> error("unexpected state") // idk why the compiler is complaining here!
    }
}
