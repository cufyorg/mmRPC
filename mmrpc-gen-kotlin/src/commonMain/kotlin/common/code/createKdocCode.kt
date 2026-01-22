package org.cufy.mmrpc.gen.kotlin.common.code

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.humanSignature
import org.cufy.mmrpc.gen.kotlin.context.Context

@ContextScope
context(ctx: Context)
fun createKdocCode(element: ElementDefinition): CodeBlock {
    return buildCodeBlock {
        if (GenFeature.KDOC_SIGNATURE in ctx.features)
            add("### %L", element.humanSignature())

        if (GenFeature.KDOC_DESCRIPTION in ctx.features)
            if (element.description.isNotBlank())
                add("\n\n%L\n", element.description)
    }
}
