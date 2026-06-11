package org.cufy.mmrpc.gen.kotlin.common.model

import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.resolveParent
import org.cufy.mmrpc.gen.kotlin.context.Context

@ContextScope
context(ctx: Context)
fun ConstDefinition.isOfSymbolicScalar(): Boolean {
    val type = this.type
    return type is ScalarDefinition && type.symbolic
}

@ContextScope
context(ctx: Context)
fun ConstDefinition.isGeneratingProperty(): Boolean {
    if (isOfSymbolicScalar()) return type.isGeneratingClass()
    val parent = resolveParent()
    if (parent is EnumDefinition && this in parent.entries) return false
    if (parent != null) return parent.isGeneratingClass()
    return GenFeature.GENERATE_TYPES in ctx.features
}

/**
 * Return the name of the property generated from [this] (assuming it has one).
 */
@ContextScope
context(ctx: Context)
fun ConstDefinition.nameOfProperty(): String {
    if (GenFeature.KEEP_CONST_PROPERTY_NAMES in ctx.features)
        return name

    return name.toScreamingSnakeCase()
}
