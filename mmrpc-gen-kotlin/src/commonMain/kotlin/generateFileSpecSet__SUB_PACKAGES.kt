package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.gen.kotlin.util.gen.debug
import org.cufy.mmrpc.gen.kotlin.util.gen.debugLog
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf

private const val TAG = "generateFileSpecSet__SUB_PACKAGES"

@Suppress("FunctionName")
internal fun generateFileSpecSet__SUB_PACKAGES(
    ctx: GenContext,
    onEachFile: FileSpec.Builder.() -> Unit,
): List<FileSpec> {
    return buildList {
        for (node in ctx.createElementNodes) {
            val ancestor = ctx.createElementNodes.find {
                node.element.namespace in it.element.asNamespace
            }

            if (ancestor != null) {
                ctx.debug {
                    val parent = ctx.createElementNodes.find {
                        node.element.namespace == it.element.asNamespace
                    }

                    if (parent == null) {
                        val fqn = node.element.canonicalName.value
                        ctx.debugLog(TAG, "Element was ignored due to it having no direct parent: $fqn")
                    }
                }

                continue
            }

            val file = FileSpec
                .builder(ctx.generatedClassOf(node.element))
                .addType(create(node, ctx))
                .apply(onEachFile)
                .build()

            add(file)
        }
    }
}

private fun create(node: CreateElementNode, ctx: GenContext): TypeSpec {
    val asNamespace = node.element.asNamespace
    val spec = node.block()

    for (it in ctx.createElementNodes) {
        if (it.element.namespace != asNamespace) continue

        spec.addType(create(it, ctx))
    }

    for (it in ctx.onElementNodes) {
        if (it.element != node.element) continue

        it.block(spec)
    }

    return spec.build()
}
