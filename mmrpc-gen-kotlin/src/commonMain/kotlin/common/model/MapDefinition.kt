package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.MapDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.context.Context

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun MapDefinition.isGeneratingTypealias(): Boolean {
    if (name[0] == '-') return false
    if (namespace !in ctx.roots) return false
    return GenFeature.GENERATE_TYPES in ctx.features
}

@ContextScope
context(ctx: Context)
fun MapDefinition.hasGeneratedTypealias(): Boolean {
    if (name[0] == '-') return false
    if (namespace !in ctx.roots) return false
    return true
}

@ContextScope
context(ctx: Context)
fun MapDefinition.generatedTypealias(): ClassName {
    debug { check(hasGeneratedTypealias()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////
