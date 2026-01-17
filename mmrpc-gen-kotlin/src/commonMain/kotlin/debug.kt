package org.cufy.mmrpc.gen.kotlin

import org.cufy.mmrpc.gen.kotlin.context.Context

@ContextScope
context(ctx: Context)
inline fun debug(block: () -> Unit) {
    if (GenFeature.DEBUG in ctx.features)
        block()
}
