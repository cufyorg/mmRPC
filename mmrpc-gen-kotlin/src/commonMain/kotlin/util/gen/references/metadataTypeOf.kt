package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val TAG = "metadataTypeOf"

/**
 * Returns the assignable kotlin-annotation-compatible type of some element.
 */
@Marker3
fun GenGroup.metadataTypeOf(element: TypeDefinition): TypeName {
    return when (element) {
        is ArrayDefinition,
        -> ARRAY.parameterizedBy(typeOf(element.arrayType))

        is ScalarDefinition,
        -> primitiveClassOf(element)

        is EnumDefinition,
        -> generatedClassOf(element)

        is OptionalDefinition,
        is UnionDefinition,
        is StructDefinition,
        is InterDefinition,
        is TupleDefinition,
        -> failGen(TAG, element) { "element not supported" }
    }
}
