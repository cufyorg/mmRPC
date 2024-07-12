package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asEnumEntryName
import org.cufy.mmrpc.gen.kotlin.util.asPropertyName
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.poet.createCall
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

private const val TAG = "createLiteral"

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [element].
 */
@Marker3
fun GenGroup.createLiteral(element: ConstDefinition): CodeBlock {
    return createLiteral(element.constType, element.constValue)
}

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [value]
 * in the given type [element].
 */
@Marker3
fun GenGroup.createLiteral(element: TypeDefinition, value: Literal): CodeBlock {
    return when (element) {
        is OptionalDefinition -> createLiteral(element.optionalType, value)
        is ArrayDefinition -> createLiteral(element, value)
        is EnumDefinition -> createLiteral(element, value)
        is ScalarDefinition -> createLiteral(element, value)
        is TupleDefinition -> createLiteral(element, value)

        is StructDefinition -> createLiteral(element, value)
        is InterDefinition -> createLiteral(element, value)
        is UnionDefinition -> createLiteral(element, value)
    }
}

// ===================={    Literal    }==================== //

private fun GenGroup.createLiteral(element: ScalarDefinition, literal: Literal): CodeBlock {
    val valueCode = when (literal) {
        is NullLiteral -> return CodeBlock.of("null")
        is BooleanLiteral -> CodeBlock.of("%L", literal.value)
        is IntLiteral -> CodeBlock.of("%L", literal.value)
        is FloatLiteral -> CodeBlock.of("%L", literal.value)
        is StringLiteral -> CodeBlock.of("%S", literal.value)
        is TupleLiteral -> failGen(TAG, element) { "illegal value: $literal" }
        is StructLiteral -> failGen(TAG, element) { "illegal value: $literal" }
    }

    if (!hasGeneratedClass(element))
        return valueCode

    return CodeBlock.of("%T(%L)", generatedClassOf(element), valueCode)
}

private fun GenGroup.createLiteral(element: EnumDefinition, literal: Literal): CodeBlock {
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (!hasGeneratedClass(element))
        failGen(TAG, element) { "enums are required to have generated classes for this to work" }

    // find an entry with the same value presented
    val winner = element.enumEntries.firstOrNull { it.constValue == literal }
    winner ?: failGen(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element), winner.asEnumEntryName)
}

// ===================={ TupleLiteral  }==================== //

private fun GenGroup.createLiteral(element: ArrayDefinition, literal: Literal): CodeBlock {
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (literal !is TupleLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    return createCallSingleVararg(
        function = CodeBlock.of("listOf"),
        literal.value.map { createLiteral(element.arrayType, it) }
    )
}

private fun GenGroup.createLiteral(element: TupleDefinition, literal: Literal): CodeBlock {
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (literal !is TupleLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    if (!hasGeneratedClass(element))
        failGen(TAG, element) { "tuples are required to have generated classes for this to work" }

    fun typeOf(position: Int): TypeDefinition {
        if (position in element.tupleTypes.indices)
            return element.tupleTypes[position]

        failGen(TAG, element) { "illegal value: $literal (too many items)" }
    }

    return createCallSingleVararg(
        function = CodeBlock.of("%T", generatedClassOf(element)),
        literal.value.mapIndexed { position, it ->
            createLiteral(typeOf(position), it)
        }
    )
}

// ===================={ StructLiteral }==================== //

private fun GenGroup.createLiteral(element: StructDefinition, literal: Literal): CodeBlock {
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (literal !is StructLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    if (!hasGeneratedClass(element))
        failGen(TAG, element) { "structures are required to have generated classes for this to work" }

    fun fieldOfOrThrow(name: String): FieldDefinition {
        val field = element.structFields.find { it.name == name }
        field ?: failGen(TAG, element) { "illegal value: $literal (unknown field $name)" }
        return field
    }

    return createCall(
        function = CodeBlock.of("%T", generatedClassOf(element)),
        literal.value.entries.associate { (name, value) ->
            val field = fieldOfOrThrow(name)
            field.asPropertyName to createLiteral(field.fieldType, value)
        }
    )
}

private fun GenGroup.createLiteral(element: InterDefinition, literal: Literal): CodeBlock {
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (literal !is StructLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    if (!hasGeneratedClass(element))
        failGen(TAG, element) { "intersections are required to have generated classes for this to work" }

    fun fieldOfOrThrow(name: String): FieldDefinition {
        for (it in element.interTypes) {
            val field = it.structFields.find { it.name == name }
            field ?: continue
            return field
        }

        failGen(TAG, element) { "illegal value: $literal (unknown field $name)" }
    }

    return createCall(
        function = CodeBlock.of("%T", generatedClassOf(element)),
        literal.value.entries.associate { (name, value) ->
            val field = fieldOfOrThrow(name)
            field.asPropertyName to createLiteral(field.fieldType, value)
        }
    )
}

private fun GenGroup.createLiteral(element: UnionDefinition, literal: Literal): CodeBlock {
    // pre-conditions and fast routes
    if (literal is NullLiteral)
        return CodeBlock.of("null")

    if (literal !is StructLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    if (element.unionTypes.isEmpty())
        failGen(TAG, element) { "illegal value: $literal (empty union type)" }

    if (element.unionTypes.size == 1)
        return createLiteral(element.unionTypes.single(), literal)

    // extracting the discriminator from `literal`
    val canonicalNameLiteral = literal.value[element.unionDiscriminator]
    canonicalNameLiteral ?: failGen(TAG, element) { "illegal value: $literal (no discriminator)" }

    if (canonicalNameLiteral !is StringLiteral)
        failGen(TAG, element) { "illegal value: $literal (non-string discriminator)" }

    val canonicalNameValue = canonicalNameLiteral.value

    // finding the suitable struct from the types of the union
    val winner = element.unionTypes.find { it.canonicalName.value == canonicalNameValue }
    winner ?: failGen(TAG, element) { "illegal value: $literal (unknown discriminator value)" }

    // delegating literal creation to the found struct
    return createLiteral(winner, literal)
}
