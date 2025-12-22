package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.*
import org.cufy.mmrpc.gen.kotlin.util.createCall
import org.cufy.mmrpc.gen.kotlin.util.createCallSingleVararg

private const val TAG = "createLiteral.kt"

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [element].
 */
@Marker3
context(ctx: GenContext)
fun createLiteral(element: ConstDefinition): CodeBlock {
    return createLiteral(element.type, element.value)
}

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [literal]
 * in the given type [element].
 */
@Marker3
context(ctx: GenContext)
fun createLiteral(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is OptionalDefinition -> createLiteralOfOptional(element, literal)
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createLiteralOfArray(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }

        is EnumDefinition -> createLiteralOfEnum(element, literal)
        is ScalarDefinition -> createLiteralOfScalar(element, literal)
        is TupleDefinition -> when (literal) {
            is TupleLiteral -> createLiteralOfTuple(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }

        is StructDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfStruct(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }

        is InterDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfInter(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }

        is UnionDefinition -> when (literal) {
            is StructLiteral -> createLiteralOfUnion(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }
    }
}

// ===================={    Literal    }==================== //

context(ctx: GenContext)
private fun createLiteralOfScalar(element: ScalarDefinition, literal: Literal): CodeBlock {
    val valueCode = when (literal) {
        is BooleanLiteral -> CodeBlock.of("%L", literal.value)
        is IntLiteral -> CodeBlock.of("%L", literal.value)
        is FloatLiteral -> CodeBlock.of("%L", literal.value)
        is StringLiteral -> CodeBlock.of("%S", literal.value)
        is NullLiteral -> fail(TAG, element) { "illegal value: $literal" }
        is TupleLiteral -> fail(TAG, element) { "illegal value: $literal" }
        is StructLiteral -> fail(TAG, element) { "illegal value: $literal" }
    }

    return when {
        isNative(element) ->
            valueCode

        isUserdefined(element) ->
            CodeBlock.of("%T(%L)", userdefinedClassOf(element), valueCode)

        hasGeneratedClass(element) ->
            CodeBlock.of("%T(%L)", generatedClassOf(element.canonicalName), valueCode)

        else ->
            fail(TAG, element) { "element not supported" }
    }
}

context(ctx: GenContext)
private fun createLiteralOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.entries.firstOrNull { it.value == literal }
    winner ?: fail(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element.canonicalName), asEnumEntryName(winner))
}

context(ctx: GenContext)
private fun createLiteralOfOptional(element: OptionalDefinition, literal: Literal): CodeBlock {
    return when (literal) {
        is NullLiteral -> CodeBlock.of("null")
        else -> createLiteral(element.type, literal)
    }
}

// ===================={ TupleLiteral  }==================== //

context(ctx: GenContext)
private fun createLiteralOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("listOf"),
        literal.value.map { createLiteral(element.type, it) }
    )
}

context(ctx: GenContext)
private fun createLiteralOfTuple(element: TupleDefinition, literal: TupleLiteral): CodeBlock {
    when (calculateTupleStrategy(element)) {
        TupleStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element.canonicalName))

        TupleStrategy.DATA_CLASS -> {
            fun typeOf(position: Int): TypeDefinition {
                if (position in element.types.indices)
                    return element.types[position]

                fail(TAG, element) { "illegal value: $literal (too many items)" }
            }

            return createCallSingleVararg(
                function = CodeBlock.of("%T", generatedClassOf(element.canonicalName)),
                literal.value.mapIndexed { position, it ->
                    createLiteral(typeOf(position), it)
                }
            )
        }
    }
}

// ===================={ StructLiteral }==================== //

context(ctx: GenContext)
private fun createLiteralOfStruct(element: StructDefinition, literal: StructLiteral): CodeBlock {
    if (element.canonicalName == builtin.Void.canonicalName)
        return CodeBlock.of("%T", Unit::class)

    when (calculateStructStrategy(element)) {
        StructStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element.canonicalName))

        StructStrategy.DATA_CLASS -> {
            fun fieldOfOrThrow(name: String): FieldDefinition {
                val field = element.fields.find { it.name == name }
                field ?: fail(TAG, element) { "illegal value: $literal (unknown field $name)" }
                return field
            }

            return createCall(
                function = CodeBlock.of("%T", generatedClassOf(element.canonicalName)),
                literal.value.entries.associate { (name, value) ->
                    val field = fieldOfOrThrow(name)
                    asPropertyName(field) to createLiteral(field.type, value)
                }
            )
        }
    }
}

context(ctx: GenContext)
private fun createLiteralOfInter(element: InterDefinition, literal: StructLiteral): CodeBlock {
    when (calculateInterStrategy(element)) {
        InterStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", generatedClassOf(element.canonicalName))

        InterStrategy.DATA_CLASS -> {
            fun fieldOfOrThrow(name: String): FieldDefinition {
                for (it in element.types) {
                    val field = it.fields.find { it.name == name }
                    field ?: continue
                    return field
                }

                fail(TAG, element) { "illegal value: $literal (unknown field $name)" }
            }

            return createCall(
                function = CodeBlock.of("%T", generatedClassOf(element.canonicalName)),
                literal.value.entries.associate { (name, value) ->
                    val field = fieldOfOrThrow(name)
                    asPropertyName(field) to createLiteral(field.type, value)
                }
            )
        }
    }
}

context(ctx: GenContext)
private fun createLiteralOfUnion(element: UnionDefinition, literal: StructLiteral): CodeBlock {
    when (calculateUnionStrategy(element)) {
        UnionStrategy.DATA_OBJECT ->
            fail(TAG, element) { "illegal value: $literal (empty union type)" }

        UnionStrategy.SEALED_INTERFACE -> {
            if (element.types.size == 1)
                return createLiteralOfStruct(element.types.single(), literal)

            // extracting the discriminator from `literal`
            val canonicalNameLiteral = literal.value[element.discriminator]
            canonicalNameLiteral ?: fail(TAG, element) { "illegal value: $literal (no discriminator)" }

            if (canonicalNameLiteral !is StringLiteral)
                fail(TAG, element) { "illegal value: $literal (non-string discriminator)" }

            val canonicalNameValue = canonicalNameLiteral.value

            // finding the suitable struct from the types of the union
            val winner = element.types.find { it.canonicalName.value == canonicalNameValue }
            winner ?: fail(TAG, element) { "illegal value: $literal (unknown discriminator value)" }

            // delegating literal creation to the found struct
            return createLiteralOfStruct(winner, literal)
        }

        UnionStrategy.WRAPPER_SEALED_INTERFACE -> {
            TODO("Union strategy not supported yet: wrapped-sealed-interface")
        }
    }
}
