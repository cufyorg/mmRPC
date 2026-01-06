package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.gen.kotlin.common.debug
import org.cufy.mmrpc.gen.kotlin.common.debugLog
import org.cufy.mmrpc.gen.kotlin.common.declarationsClassOf
import org.cufy.mmrpc.gen.kotlin.common.generatedClassName
import org.cufy.mmrpc.gen.kotlin.util.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.fetchKind
import org.cufy.mmrpc.gen.kotlin.util.fileSpec

private const val TAG = "generateFileSpecSet__SUB_PACKAGES"

@Suppress("FunctionName")
context(ctx: GenContext)
internal fun generateFileSpecSet__SUB_PACKAGES(
    onEachFile: FileSpec.Builder.() -> Unit,
): List<FileSpec> {
    val types = ctx.createTypeNodes.map { it.canonicalName }
    val files = ctx.injectFileNodes.map { it.canonicalName } +
            (ctx.injectScopeNodes.map { it.canonicalName } - types.toSet())

    val createTypeNodeMap = ctx.createTypeNodes
        .groupBy { node ->
            types.asSequence()
                .filter { node.canonicalName in it }
                .maxOrNull()
        }
    val injectTypeNodeMap = ctx.injectTypeNodes
        .groupBy { it.canonicalName }
    val injectFileNodeMap = ctx.injectFileNodes
        .groupBy { it.canonicalName }
    val injectScopeNodeMap = ctx.injectScopeNodes
        .groupBy { it.canonicalName }

    debug {
        for (cn in injectTypeNodeMap.keys) if (cn !in types)
            debugLog(TAG, "Element was ignored due to it having no direct parent: $cn")
    }

    fun createTypeSpec(node: CreateTypeNode): TypeSpec {
        val spec = node.block()

        createTypeNodeMap[node.canonicalName]
            ?.forEach { spec.addType(createTypeSpec(it)) }
        injectTypeNodeMap[node.canonicalName]
            ?.forEach { it.block(spec) }

        if (!injectScopeNodeMap[node.canonicalName].isNullOrEmpty()) {
            if (spec.fetchKind() == TypeSpec.Kind.OBJECT) {
                injectScopeNodeMap[node.canonicalName]
                    ?.forEach { it.block(spec) }
            } else {
                spec.addType(companionObjectSpec {
                    injectScopeNodeMap[node.canonicalName]
                        ?.forEach { it.block(this) }
                })
            }
        }

        return spec.build()
    }

    return buildList {
        for (node in createTypeNodeMap[null].orEmpty()) {
            add(fileSpec(node.canonicalName.generatedClassName()) {
                addType(createTypeSpec(node))
                onEachFile()
            })
        }

        for (canonicalName in files) {
            add(fileSpec(declarationsClassOf(canonicalName)) {
                injectFileNodeMap[canonicalName]
                    ?.forEach { it.block(this) }
                injectScopeNodeMap[canonicalName]
                    ?.forEach { it.block(this) }
                onEachFile()
            })
        }
    }
}
