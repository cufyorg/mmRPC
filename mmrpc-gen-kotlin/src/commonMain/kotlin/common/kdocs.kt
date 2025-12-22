package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenContext

@Marker3
context(ctx: GenContext)
fun createKDoc(element: ElementDefinition): CodeBlock {
    return CodeBlock.Builder().apply {
        add(buildString {
            append("### ")
            append(signatureOf(element))

            if (element.description.isNotBlank()) {
                appendLine()
                appendLine()
                append(element.description)
                appendLine()
            }
        })
    }.build()
}

@Marker3
context(ctx: GenContext)
fun createKDocShort(element: ElementDefinition): CodeBlock {
    if (hasGeneratedClass(element))
        return CodeBlock.of("@see [%L]", generatedClassOf(element.canonicalName))

    if (element is ScalarDefinition)
        if (isNative(element) || isUserdefined(element))
            return CodeBlock.of("### %L", signatureOf(element))

    if (element is MetadataDefinition)
        if (isNative(element) || isUserdefined(element))
            return CodeBlock.of("### %L", signatureOf(element))

    return createKDoc(element)
}

/**
 * Return a human-readable name of the given [element].
 */
fun signatureOf(element: ElementDefinition): String {
    val discriminator = when (element) {
        is ArrayDefinition -> "array"
        is EnumDefinition -> "enum"
        is ConstDefinition -> "const"
        is FaultDefinition -> "fault"
        is FieldDefinition -> "field"
        is InterDefinition -> "inter"
        is MetadataDefinition -> "metadata"
        is OptionalDefinition -> "optional"
        is ProtocolDefinition -> "protocol"
        is RoutineDefinition -> "routine"
        is ScalarDefinition -> "scalar"
        is StructDefinition -> "struct"
        is TupleDefinition -> "tuple"
        is UnionDefinition -> "union"
    }

    return "$discriminator ${element.canonicalName.value}"
}
