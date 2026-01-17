package org.cufy.mmrpc.gen.kotlin.context

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.gen.kotlin.EmitScope
import org.cufy.mmrpc.gen.kotlin.InjectNode
import org.cufy.mmrpc.gen.kotlin.NodeList

class FinalStage(val nodes: NodeList) {
    val injected = mutableListOf<InjectNode<*>>()
    val files = mutableMapOf<ClassName?, FileSpec.Builder>()
}

@EmitScope
context(ctx: FinalStage)
fun <T : Any> T.applyOf(
    dummy: Unit = Unit,
    target: CanonicalName?,
) {
    for (node in ctx.nodes.injections) {
        if (node.target != target)
            continue
        if (!node.type.isInstance(this))
            continue
        if (node in ctx.injected)
            error("Node double injection")

        @Suppress("UNCHECKED_CAST")
        node as InjectNode<T>
        node.injection(this)
        ctx.injected += node
    }
}

@EmitScope
context(ctx: FinalStage)
fun add(
    dummy: Unit = Unit,
    pkg: String,
    name: String,
    block: FileSpec.Builder.() -> Unit
) {
    val cn = ClassName(pkg, name)
    ctx.files.getOrPut(cn) { FileSpec.builder(cn) }.block()
}
