package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.gen.kotlin.common.debug
import org.cufy.mmrpc.gen.kotlin.common.debugLog
import org.cufy.mmrpc.gen.kotlin.common.declarationsClassOf
import org.cufy.mmrpc.gen.kotlin.common.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.fetchKind
import org.cufy.mmrpc.gen.kotlin.util.fileSpec

private const val TAG = "generateFileSpecSet__SUB_PACKAGES"

@Suppress("FunctionName")
internal fun generateFileSpecSet__SUB_PACKAGES(
    ctx: GenContext,
    onEachFile: FileSpec.Builder.() -> Unit,
): List<FileSpec> {
    val types = ctx.createTypeNodes.map { it.canonicalName }

    val createTypeNodeMap = ctx.createTypeNodes
        .groupBy { node ->
            types.asSequence()
                .filter { node.canonicalName in it }
                .maxOrNull()
        }
    val injectTypeNodeMap = ctx.injectTypeNodes
        .groupBy { it.canonicalName }
    val injectScopeNodeMap = ctx.injectScopeNodes
        .groupBy { it.canonicalName }

    ctx.debug {
        for (cn in injectTypeNodeMap.keys) if (cn !in types)
            ctx.debugLog(TAG, "Element was ignored due to it having no direct parent: $cn")
    }

    fun createTypeSpec(node: CreateTypeNode): TypeSpec {
        val spec = node.block()

        createTypeNodeMap[node.canonicalName].orEmpty()
            .forEach { spec.addType(createTypeSpec(it)) }
        injectTypeNodeMap[node.canonicalName].orEmpty()
            .forEach { it.block(spec) }

        if (!injectScopeNodeMap[node.canonicalName].isNullOrEmpty()) {
            if (spec.fetchKind() == TypeSpec.Kind.OBJECT) {
                injectScopeNodeMap[node.canonicalName].orEmpty()
                    .forEach { it.block(spec) }
            } else {
                spec.addType(companionObjectSpec {
                    injectScopeNodeMap[node.canonicalName].orEmpty()
                        .forEach { it.block(this) }
                })
            }
        }

        return spec.build()
    }

    return buildList {
        for (node in createTypeNodeMap[null].orEmpty()) {
            add(fileSpec(ctx.generatedClassOf(node.canonicalName)) {
                addType(createTypeSpec(node))
                onEachFile()
            })
        }

        for ((canonicalName, nodes) in injectScopeNodeMap) {
            if (canonicalName in types) continue

            add(fileSpec(ctx.declarationsClassOf(canonicalName)) {
                nodes.forEach { it.block(this) }
                onEachFile()
            })
        }
    }
}
