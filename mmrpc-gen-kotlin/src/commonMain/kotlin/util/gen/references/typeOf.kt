package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative
import org.cufy.mmrpc.gen.kotlin.util.gen.isUserdefined

/**
 * Returns the assignable type of some element.
 */
@Marker3
fun GenScope.typeOf(element: TypeDefinition): TypeName {
    return when (element) {
        is OptionalDefinition,
        -> typeOf(element.optionalType).copy(nullable = true)

        is ArrayDefinition,
        -> LIST.parameterizedBy(typeOf(element.arrayType))

        is ScalarDefinition,
        -> when {
            isUserdefined(element) -> userdefinedClassOf(element)
            isNative(element) -> nativeClassOf(element)
            hasGeneratedClass(element) -> generatedClassOf(element)
            else -> ANY
        }

        is UnionDefinition,
        is StructDefinition,
        is EnumDefinition,
        is InterDefinition,
        is TupleDefinition,
        -> when {
            hasGeneratedClass(element) -> generatedClassOf(element)
            else -> ANY
        }
    }
}
