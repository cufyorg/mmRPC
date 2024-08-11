package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.references.refOfINFOOrCreateInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.createBoxedLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.createCall
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

/**
 * Return code that, when executed, returns [MetadataInfoUsage] representing the given [element].
 */
@Marker3
fun GenGroup.createInfoUsage(element: MetadataDefinitionUsage): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataInfoUsage::class),
        "info" to refOfINFOOrCreateInfo(element.definition),
        "fields" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.fields.map { createInfoUsage(it) }
        ),
    )
}

/**
 * Return code that, when executed, returns [FieldInfoUsage] representing the given [element].
 */
@Marker3
fun GenGroup.createInfoUsage(element: FieldDefinitionUsage): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", FieldInfoUsage::class),
        "info" to refOfINFOOrCreateInfo(element.definition),
        "value" to createBoxedLiteral(element.value),
    )
}
