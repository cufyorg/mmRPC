package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createInfoUsage(element: MetadataDefinitionUsage): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataInfoUsage::class),
        "info" to refOfInfoOrCreateInfo(element.definition),
        "parameters" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.parameters.map { createInfoUsage(it) }
        ),
    )
}

@Marker0
fun GenGroup.createInfoUsage(element: MetadataParameterDefinitionUsage): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataParameterInfoUsage::class),
        "info" to refOfInfoOrCreateInfo(element.definition),
        "value" to createBoxedLiteral(element.value),
    )
}
