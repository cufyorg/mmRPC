package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative

/**
 * Returns the assignable type of some element.
 */
@Marker3
fun GenGroup.typeOf(element: TypeDefinition): TypeName {
    if (element.isAnonymous)
        return ANY.copy(nullable = true)

    return when (element) {
        is OptionalDefinition -> typeOf(element.optionalType).copy(nullable = true)
        is ArrayDefinition -> LIST.parameterizedBy(typeOf(element.arrayType))

        is ScalarDefinition -> when {
            isNative(element) -> nativeClassOf(element)
            else -> generatedClassOf(element)
        }

        is UnionDefinition -> generatedClassOf(element)
        is StructDefinition -> generatedClassOf(element)
        is EnumDefinition -> generatedClassOf(element)

        is InterDefinition -> generatedClassOf(element)
        is TupleDefinition -> generatedClassOf(element)
    }
}
