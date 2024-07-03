package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createInfo(element: HttpEndpointDefinition): CodeBlock {
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

@Marker0
fun GenGroup.createInfo(element: IframeEndpointDefinition): CodeBlock {
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

@Marker0
fun GenGroup.createInfo(element: KafkaEndpointDefinition): CodeBlock {
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
            else refOfInfoOrCreateInfo(it)
        },
    )
}

@Marker0
fun GenGroup.createInfo(element: KafkaPublicationEndpointDefinition): CodeBlock {
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
            else refOfInfoOrCreateInfo(it)
        },
    )
}
