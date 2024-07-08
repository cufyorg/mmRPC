package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createInfo(element: ArrayDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ArrayInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfInfoOrCreateInfo(element.arrayType),
    )
}

@Marker0
fun GenGroup.createInfo(element: ConstDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ConstInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfInfoOrCreateInfo(element.constType),
        "value" to createBoxedLiteral(element.constValue),
    )
}

@Marker0
fun GenGroup.createInfo(element: InterDefinition): CodeBlock {
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
            element.interTypes.map { refOfInfoOrCreateInfo(it) }
        ),
    )
}

@Marker0
fun GenGroup.createInfo(element: OptionalDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", OptionalInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfInfoOrCreateInfo(element.optionalType),
    )
}

@Marker0
fun GenGroup.createInfo(element: ScalarDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ScalarInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
    )
}

@Marker0
fun GenGroup.createInfo(element: StructDefinition): CodeBlock {
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
            element.structFields.map { refOfInfoOrCreateInfo(it) }
        ),
    )
}

@Marker0
fun GenGroup.createInfo(element: TupleDefinition): CodeBlock {
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
            element.tupleTypes.map { refOfInfoOrCreateInfo(it) }
        ),
    )
}

@Marker0
fun GenGroup.createInfo(element: UnionDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", UnionInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "types" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.unionTypes.map { refOfInfoOrCreateInfo(it) }
        ),
    )
}
