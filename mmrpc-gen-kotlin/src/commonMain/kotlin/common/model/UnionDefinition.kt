package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import net.pearx.kasechange.toPascalCase
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.UnionStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun UnionDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

/**
 * Assuming [this] is a member of some union,
 * this is the name of the generated entry by [this].
 */
@ContextScope
context(ctx: Context)
fun ElementDefinition.nameOfUnionWrapperEntry(): String {
    if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
        return name

    return name.toPascalCase()
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun UnionDefinition.isSealed(): Boolean {
    val pkg = canonicalName.assumedPackageName()
    for (it in types) {
        if (!it.isGeneratingClass())
            return false
        if (pkg != it.canonicalName.assumedPackageName())
            return false
    }
    return true
}

@ContextScope
context(ctx: Context)
fun UnionDefinition.calculateStrategy(): UnionStrategy {
    if (types.isEmpty()) return UnionStrategy.DATA_OBJECT
    if (!isSealed()) return UnionStrategy.WRAPPER_SEALED_INTERFACE
    return UnionStrategy.SEALED_INTERFACE
}

////////////////////////////////////////
