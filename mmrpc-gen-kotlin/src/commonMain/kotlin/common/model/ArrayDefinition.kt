package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ArrayDefinition.isGeneratingTypealias(): Boolean {
    if (canonicalName.isAnonymous) return false
    if (namespace !in ctx.roots) return false
    return GenFeature.GENERATE_TYPES in ctx.features
}

@ContextScope
context(ctx: Context)
fun ArrayDefinition.hasGeneratedTypealias(): Boolean {
    if (canonicalName.isAnonymous) return false
    if (namespace !in ctx.roots) return false
    return true
}

@ContextScope
context(ctx: Context)
fun ArrayDefinition.generatedTypealias(): TypeName {
    debug { check(hasGeneratedTypealias()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////
