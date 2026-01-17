package org.cufy.mmrpc.gen.kotlin.common.code

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.*
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.context.fail
import org.cufy.mmrpc.gen.kotlin.util.createCall
import org.cufy.mmrpc.gen.kotlin.util.createCallSingleVararg

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [literal]
 * in the given type [element].
 */
@ContextScope
context(ctx: Context)
fun createLiteralCode(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is OptionalDefinition -> createLiteralCodeOfOptional(element, literal)
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createLiteralCodeOfArray(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is MapDefinition -> when (literal) {
            is StructLiteral -> createLiteralCodeOfMap(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is EnumDefinition -> createLiteralCodeOfEnum(element, literal)
        is ScalarDefinition -> createLiteralCodeOfScalar(element, literal)
        is TupleDefinition -> when (literal) {
            is TupleLiteral -> createLiteralCodeOfTuple(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is StructDefinition -> when (literal) {
            is StructLiteral -> createLiteralCodeOfStruct(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is InterDefinition -> when (literal) {
            is StructLiteral -> createLiteralCodeOfInter(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is UnionDefinition -> when (literal) {
            is StructLiteral -> createLiteralCodeOfUnion(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is TraitDefinition -> when (literal) {
            is StructLiteral -> createLiteralCodeOfTrait(element, literal)
            else -> fail(element, "illegal value: $literal")
        }
    }
}

// ===================={    Literal    }==================== //

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfScalar(element: ScalarDefinition, literal: Literal): CodeBlock {
    val valueCode = when (literal) {
        is BooleanLiteral -> CodeBlock.of("%L", literal.value)
        is IntLiteral -> CodeBlock.of("%L", literal.value)
        is FloatLiteral -> CodeBlock.of("%L", literal.value)
        is StringLiteral -> CodeBlock.of("%S", literal.value)
        is NullLiteral -> fail(element, "illegal value: $literal")
        is TupleLiteral -> fail(element, "illegal value: $literal")
        is StructLiteral -> fail(element, "illegal value: $literal")
    }

    if (element.isNative())
        return CodeBlock.of("%L", valueCode)

    return CodeBlock.of("%T(%L)", element.className(), valueCode)
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.entries.firstOrNull { it.value == literal }
    winner ?: fail(element, "illegal value: $literal (enum entry not found)")

    // create a reference to that entry
    return CodeBlock.of("%T.%L", element.generatedClassName(), winner.nameOfEnumEntry())
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfOptional(element: OptionalDefinition, literal: Literal): CodeBlock {
    return when (literal) {
        is NullLiteral -> CodeBlock.of("null")
        else -> createLiteralCode(element.type, literal)
    }
}

// ===================={ TupleLiteral  }==================== //

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("listOf"),
        literal.value.map { createLiteralCode(element.type, it) }
    )
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfMap(element: MapDefinition, literal: StructLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("mapOf"),
        literal.value.map { (key, value) ->
            CodeBlock.of("%S to %L", key, createLiteralCode(element.type, value))
        }
    )
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfTuple(element: TupleDefinition, literal: TupleLiteral): CodeBlock {
    when (element.calculateStrategy()) {
        TupleStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", element.generatedClassName())

        TupleStrategy.DATA_CLASS -> {
            fun typeOf(position: Int): TypeDefinition {
                if (position in element.types.indices)
                    return element.types[position]

                fail(element, "illegal value: $literal (too many items)")
            }

            return createCallSingleVararg(
                function = CodeBlock.of("%T", element.generatedClassName()),
                literal.value.mapIndexed { position, it ->
                    createLiteralCode(typeOf(position), it)
                }
            )
        }
    }
}

// ===================={ StructLiteral }==================== //

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfStruct(element: StructDefinition, literal: StructLiteral): CodeBlock {
    if (element.canonicalName == builtin.Unit.canonicalName)
        return CodeBlock.of("%T", Unit::class)

    when (element.calculateStrategy()) {
        StructStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", element.generatedClassName())

        StructStrategy.DATA_CLASS -> {
            fun fieldOfOrThrow(name: String): FieldDefinition {
                val field = element.fields.find { it.name == name }
                field ?: fail(element, "illegal value: $literal (unknown field $name)")
                return field
            }

            return createCall(
                function = CodeBlock.of("%T", element.generatedClassName()),
                literal.value.entries.associate { (name, value) ->
                    val field = fieldOfOrThrow(name)
                    field.nameOfProperty() to createLiteralCode(field.type, value)
                }
            )
        }
    }
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfInter(element: InterDefinition, literal: StructLiteral): CodeBlock {
    when (element.calculateStrategy()) {
        InterStrategy.DATA_OBJECT ->
            return CodeBlock.of("%T", element.generatedClassName())

        InterStrategy.DATA_CLASS -> {
            fun fieldOfOrThrow(name: String): FieldDefinition {
                for (it in element.types) {
                    val field = it.fields.find { it.name == name }
                    field ?: continue
                    return field
                }

                fail(element, "illegal value: $literal (unknown field $name)")
            }

            return createCall(
                function = CodeBlock.of("%T", element.generatedClassName()),
                literal.value.entries.associate { (name, value) ->
                    val field = fieldOfOrThrow(name)
                    field.nameOfProperty() to createLiteralCode(field.type, value)
                }
            )
        }
    }
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfUnion(element: UnionDefinition, literal: StructLiteral): CodeBlock {
    when (element.calculateStrategy()) {
        UnionStrategy.DATA_OBJECT ->
            fail(element, "illegal value: $literal (empty union type)")

        UnionStrategy.SEALED_INTERFACE -> {
            if (element.types.size == 1)
                return createLiteralCodeOfStruct(element.types.single(), literal)

            // extracting the discriminator from `literal`
            val canonicalNameLiteral = literal.value[element.discriminator]
            canonicalNameLiteral ?: fail(element, "illegal value: $literal (no discriminator)")

            if (canonicalNameLiteral !is StringLiteral)
                fail(element, "illegal value: $literal (non-string discriminator)")

            val canonicalNameValue = canonicalNameLiteral.value

            // finding the suitable struct from the types of the union
            val winner = element.types.find { it.canonicalName.value == canonicalNameValue }
            winner ?: fail(element, "illegal value: $literal (unknown discriminator value)")

            // delegating literal creation to the found struct
            return createLiteralCodeOfStruct(winner, literal)
        }

        UnionStrategy.WRAPPER_SEALED_INTERFACE -> {
            TODO("Union strategy not supported yet: wrapped-sealed-interface")
        }
    }
}

@ContextScope
context(ctx: Context)
private fun createLiteralCodeOfTrait(element: TraitDefinition, literal: StructLiteral): CodeBlock {
    val structs = element.collectAllSubstructs().toList()

    if (structs.size == 1)
        return createLiteralCodeOfStruct(structs.single(), literal)

    // extracting the discriminator from `literal`
    val canonicalNameLiteral = literal.value[element.discriminator]
    canonicalNameLiteral ?: fail(element, "illegal value: $literal (no discriminator)")

    if (canonicalNameLiteral !is StringLiteral)
        fail(element, "illegal value: $literal (non-string discriminator)")

    val canonicalNameValue = canonicalNameLiteral.value

    // finding the suitable struct from the types of the union
    val winner = structs.find { it.canonicalName.value == canonicalNameValue }
    winner ?: fail(element, "illegal value: $literal (unknown discriminator value)")

    // delegating literal creation to the found struct
    return createLiteralCodeOfStruct(winner, literal)
}
