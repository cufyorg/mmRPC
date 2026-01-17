package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.UNIT
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.builtin
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.StructStrategy
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun StructDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

@ContextScope
context(ctx: Context)
fun StructDefinition.className(): ClassName {
    if (canonicalName == builtin.Unit.canonicalName) return UNIT
    return generatedClassName()
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun StructDefinition.collectAllSupFields(): Sequence<FieldDefinition> {
    return traits.asSequence()
        .flatMap { it.collectAllSupFields() + it.fields }
        .distinct()
}

@ContextScope
context(ctx: Context)
fun StructDefinition.collectAllFields(): Sequence<FieldDefinition> {
    return fields.asSequence() + collectAllSupFields()
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun StructDefinition.calculateStrategy(): StructStrategy {
    debug { check(hasGeneratedClass()) }

    if (fields.isEmpty() && collectAllSupFields().none())
        return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}

////////////////////////////////////////
