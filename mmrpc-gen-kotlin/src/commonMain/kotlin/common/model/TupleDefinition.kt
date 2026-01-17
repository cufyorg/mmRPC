package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.TupleStrategy
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun TupleDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun TupleDefinition.calculateStrategy(): TupleStrategy {
    if (types.isEmpty()) return TupleStrategy.DATA_OBJECT
    return TupleStrategy.DATA_CLASS
}

////////////////////////////////////////
