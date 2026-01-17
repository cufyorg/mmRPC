package org.cufy.mmrpc.gen.kotlin.common.model

import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.resolveParent
import org.cufy.mmrpc.gen.kotlin.context.Context

@ContextScope
context(ctx: Context)
fun ConstDefinition.isGeneratingProperty(): Boolean {
    val parent = resolveParent()
    if (parent != null) return parent.isGeneratingClass()
    return GenFeature.GENERATE_TYPES in ctx.features
}

/**
 * Return the name of the property generated from [this] (assuming it has one).
 */
fun ConstDefinition.nameOfProperty(): String {
    return name.toScreamingSnakeCase()
}
