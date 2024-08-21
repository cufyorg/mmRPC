package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.references.refOfINFOOrCreateInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.createBoxedLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.createBoxedNamespace
import org.cufy.mmrpc.gen.kotlin.util.poet.createCall
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

/**
 * Return code that, when executed, returns [ConstInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: ConstDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ConstInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfINFOOrCreateInfo(element.constType),
        "value" to createBoxedLiteral(element.constValue),
    )
}

/**
 * Return code that, when executed, returns [FaultInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: FaultDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", FaultInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [FieldInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: FieldDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", FieldInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfINFOOrCreateInfo(element.fieldType),
        "default" to element.fieldDefault.let {
            if (it == null) CodeBlock.of("null")
            else createBoxedLiteral(it)
        },
    )
}

/**
 * Return code that, when executed, returns [MetadataInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: MetadataDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "fields" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadataFields.map { createInfo(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [ProtocolInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: ProtocolDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ProtocolInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "routines" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.protocolRoutines.map { refOfINFOOrCreateInfo(it) }
        )
    )
}

/**
 * Return code that, when executed, returns [RoutineInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: RoutineDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", RoutineInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "endpoints" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.routineEndpoints.map { refOfINFOOrCreateInfo(it) }
        ),
        "fault" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.routineFaultUnion.map { refOfINFOOrCreateInfo(it) }
        ),
        "input" to refOfINFOOrCreateInfo(element.routineInput),
        "output" to refOfINFOOrCreateInfo(element.routineOutput),
        "key" to element.routineKey.let {
            if (it == null) CodeBlock.of("null")
            else createCallSingleVararg(
                function = CodeBlock.of("listOf"),
                it.map { n -> CodeBlock.of("%s", n) }
            )
        },
    )
}
