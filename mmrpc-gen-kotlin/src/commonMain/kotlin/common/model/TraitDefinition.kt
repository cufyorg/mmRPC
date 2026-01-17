package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.TraitStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun TraitDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun TraitDefinition.collectAllSupFields(): Sequence<FieldDefinition> {
    return traits.asSequence()
        .flatMap { it.collectAllSupFields() + it.fields }
        .distinct()
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.collectImmediateSubstructs(): Sequence<StructDefinition> {
    return ctx.elements.asSequence()
        .filterIsInstance<StructDefinition>()
        .filter { this in it.traits }
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.collectImmediateSubtraits(): Sequence<TraitDefinition> {
    return ctx.elements.asSequence()
        .filterIsInstance<TraitDefinition>()
        .filter { this in it.traits }
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.collectAllSubtraits(): Sequence<TraitDefinition> {
    val seq = collectImmediateSubtraits()
    return (seq + seq.flatMap { it.collectImmediateSubtraits() })
        .distinct()
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.collectAllSubstructs(): Sequence<StructDefinition> {
    return collectAllSubtraits()
        .flatMap { it.collectImmediateSubstructs() }
        .distinct()
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.isSealed(): Boolean {
    val pkg = canonicalName.assumedPackageName()
    for (it in collectImmediateSubstructs()) {
        if (!it.isGeneratingClass())
            return false
        if (pkg != it.canonicalName.assumedPackageName())
            return false
    }
    for (it in collectImmediateSubtraits()) {
        if (!it.isGeneratingClass())
            return false
        if (pkg != it.canonicalName.assumedPackageName())
            return false
    }
    return true
}

@ContextScope
context(ctx: Context)
fun TraitDefinition.calculateStrategy(): TraitStrategy {
    if (!isSealed()) return TraitStrategy.INTERFACE
    return TraitStrategy.SEALED_INTERFACE
}

////////////////////////////////////////
