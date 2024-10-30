package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.references.refOfINFOOrCreateInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.createBoxedNamespace
import org.cufy.mmrpc.gen.kotlin.util.poet.createCall
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

/**
 * Return code that, when executed, returns [ArrayInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: ArrayDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ArrayInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfINFOOrCreateInfo(element.arrayType),
    )
}

/**
 * Return code that, when executed, returns [EnumInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: EnumDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", EnumInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfINFOOrCreateInfo(element.enumType),
        "entries" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.enumEntries.map { refOfINFOOrCreateInfo(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [InterInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: InterDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", InterInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "types" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.interTypes.map { refOfINFOOrCreateInfo(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [OptionalInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: OptionalDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", OptionalInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfINFOOrCreateInfo(element.optionalType),
    )
}

/**
 * Return code that, when executed, returns [ScalarInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: ScalarDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ScalarInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to when (val scalarType = element.scalarType) {
            null -> CodeBlock.of("null")
            else -> refOfINFOOrCreateInfo(scalarType)
        },
    )
}

/**
 * Return code that, when executed, returns [StructInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: StructDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", StructInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "fields" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.structFields.map { createInfo(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [TupleInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: TupleDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", TupleInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "types" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.tupleTypes.map { refOfINFOOrCreateInfo(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [UnionInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: UnionDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", UnionInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "discriminator" to CodeBlock.of("%S", element.unionDiscriminator),
        "types" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.unionTypes.map { refOfINFOOrCreateInfo(it) }
        ),
    )
}
