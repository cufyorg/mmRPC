package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.references.refOfINFOOrCreateInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.createBoxedNamespace
import org.cufy.mmrpc.gen.kotlin.util.poet.createCall
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

/**
 * Return code that, when executed, returns [HttpEndpointInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: HttpEndpointDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", HttpEndpointInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "path" to element.endpointPath.let {
            CodeBlock.of("%T(%S)", HttpPath::class, it.value)
        },
        "method" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.endpointMethodUnion.map {
                CodeBlock.of("%T(%S)", HttpMethod::class, it.name)
            }
        ),
        "security" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.endpointSecurityInter.map {
                CodeBlock.of("%T(%S)", HttpSecurity::class, it.name)
            }
        ),
    )
}

/**
 * Return code that, when executed, returns [IframeEndpointInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: IframeEndpointDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", IframeEndpointInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "path" to element.endpointPath.let {
            CodeBlock.of("%T(%S)", IframePath::class, it.value)
        },
        "security" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.endpointSecurityInter.map {
                CodeBlock.of("%T(%S)", IframeSecurity::class, it.name)
            }
        ),
    )
}

/**
 * Return code that, when executed, returns [KafkaEndpointInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: KafkaEndpointDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", KafkaEndpointInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "topic" to element.endpointTopic.let {
            CodeBlock.of("%T(%S)", KafkaTopic::class, it.value)
        },
        "security" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.endpointSecurityInter.map {
                CodeBlock.of("%T(%S)", KafkaSecurity::class, it.name)
            }
        ),
        "key" to element.endpointKey.let {
            if (it == null) CodeBlock.of("null")
            else refOfINFOOrCreateInfo(it)
        },
    )
}

/**
 * Return code that, when executed, returns [KafkaPublicationEndpointInfo] representing the given [element].
 */
@Marker3
fun GenScope.createInfo(element: KafkaPublicationEndpointDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", KafkaPublicationEndpointInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "topic" to element.endpointTopic.let {
            CodeBlock.of("%T(%S)", KafkaPublicationTopic::class, it.value)
        },
        "security" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.endpointSecurityInter.map {
                CodeBlock.of("%T(%S)", KafkaPublicationSecurity::class, it.name)
            }
        ),
        "key" to element.endpointKey.let {
            if (it == null) CodeBlock.of("null")
            else refOfINFOOrCreateInfo(it)
        },
    )
}
