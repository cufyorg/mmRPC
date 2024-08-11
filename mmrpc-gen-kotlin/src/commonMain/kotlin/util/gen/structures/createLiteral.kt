package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asEnumEntryName
import org.cufy.mmrpc.gen.kotlin.util.asPropertyName
import org.cufy.mmrpc.gen.kotlin.util.gen.*
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.gen.references.userdefinedClassOf
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
 * returns the representation of the given [literal]
 * in the given type [element].
 */
@Marker3
fun GenGroup.createLiteral(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is OptionalDefinition -> createLiteralOfOptional(element, literal)
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createLiteralOfArray(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }

        is EnumDefinition -> createLiteralOfEnum(element, literal)
        is ScalarDefinition -> createLiteralOfScalar(element, literal)
        is TupleDefinition -> when (literal) {
            is TupleLiteral -> createLiteralOfTuple(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }

        is StructDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfStruct(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }

        is InterDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfInter(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }

        is UnionDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfUnion(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }
    }
}

// ===================={    Literal    }==================== //

private fun GenGroup.createLiteralOfScalar(element: ScalarDefinition, literal: Literal): CodeBlock {
    val valueCode = when (literal) {
        is BooleanLiteral -> CodeBlock.of("%L", literal.value)
        is IntLiteral -> CodeBlock.of("%L", literal.value)
        is FloatLiteral -> CodeBlock.of("%L", literal.value)
        is StringLiteral -> CodeBlock.of("%S", literal.value)
        is NullLiteral -> failGen(TAG, element) { "illegal value: $literal" }
        is TupleLiteral -> failGen(TAG, element) { "illegal value: $literal" }
        is StructLiteral -> failGen(TAG, element) { "illegal value: $literal" }
    }

    return when {
        isNative(element) ->
            valueCode

        isUserdefined(element) ->
            CodeBlock.of("%T(%L)", userdefinedClassOf(element), valueCode)

        hasGeneratedClass(element) ->
            CodeBlock.of("%T(%L)", generatedClassOf(element), valueCode)

        else ->
            failGen(TAG, element) { "element not supported" }
    }
}

private fun GenGroup.createLiteralOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.enumEntries.firstOrNull { it.constValue == literal }
    winner ?: failGen(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element), winner.asEnumEntryName)
}

private fun GenGroup.createLiteralOfOptional(element: OptionalDefinition, literal: Literal): CodeBlock {
    return when (literal) {
        is NullLiteral -> CodeBlock.of("null")
        else -> createLiteral(element.optionalType, literal)
    }
}

// ===================={ TupleLiteral  }==================== //

private fun GenGroup.createLiteralOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("listOf"),
        literal.value.map { createLiteral(element.arrayType, it) }
    )
}

private fun GenGroup.createLiteralOfTuple(element: TupleDefinition, literal: TupleLiteral): CodeBlock {
    when (calculateTupleStrategy(element)) {
        TupleStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element))

        TupleStrategy.DATA_CLASS -> {
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
    }
}

// ===================={ StructLiteral }==================== //

private fun GenGroup.createLiteralOfStruct(element: StructDefinition, literal: StructLiteral): CodeBlock {
    when (calculateStructStrategy(element)) {
        StructStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element))

        StructStrategy.DATA_CLASS -> {
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
    }
}

private fun GenGroup.createLiteralOfInter(element: InterDefinition, literal: StructLiteral): CodeBlock {
    when (calculateInterStrategy(element)) {
        InterStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element))

        InterStrategy.DATA_CLASS -> {
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
    }
}

private fun GenGroup.createLiteralOfUnion(element: UnionDefinition, literal: StructLiteral): CodeBlock {
    when (calculateUnionStrategy(element)) {
        UnionStrategy.DATA_OBJECT ->
            failGen(TAG, element) { "illegal value: $literal (empty union type)" }

        UnionStrategy.SEALED_INTERFACE -> {
            if (element.unionTypes.size == 1)
                return createLiteralOfStruct(element.unionTypes.single(), literal)

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
            return createLiteralOfStruct(winner, literal)
        }

        UnionStrategy.WRAPPER_SEALED_INTERFACE -> {
            TODO("Union strategy not supported yet: wrapped-sealed-interface")
        }
    }
}
