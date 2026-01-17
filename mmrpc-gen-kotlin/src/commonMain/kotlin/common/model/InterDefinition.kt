package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.InterStrategy
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun InterDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun InterDefinition.calculateStrategy(): InterStrategy {
    if (types.all { it.fields.isEmpty() })
        return InterStrategy.DATA_OBJECT

    return InterStrategy.DATA_CLASS
}

////////////////////////////////////////
