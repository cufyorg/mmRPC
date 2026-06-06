package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import net.pearx.kasechange.toPascalCase
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.TraitStrategy
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.util.*

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun TraitDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

/**
 * Assuming [this] is a struct of some trait,
 * this is the name of the generated type entry by [this].
 */
@ContextScope
context(ctx: Context)
fun ElementDefinition.nameOfTraitTypeEnumEntry(): String {
    if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
        return name

    return name.toPascalCase()
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
    return (sequenceOf(this) + collectAllSubtraits())
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

@ContextScope
context(ctx: Context)
fun TraitDefinition.typeEnumSpec(): TypeSpec {
    return enumClassSpec("Type") {
        for (substruct in collectAllSubstructs()) {
            addEnumConstant(substruct.nameOfTraitTypeEnumEntry(), anonymousClassSpec {
                addAnnotation(createSerialName(substruct.canonicalName.value))
            })
        }

        addCompanionObject {
            addProperty(propertySpec("type", ClassName("", "Type")) {
                receiver(generatedClassName())
                getter(getterSpec {
                    addCode(buildCodeBlock {
                        beginControlFlow("return when (this)")
                        for (substruct in collectAllSubstructs()) {
                            addStatement(
                                "is %L -> Type.%L",
                                substruct.generatedClassName(),
                                substruct.nameOfTraitTypeEnumEntry()
                            )
                        }
                        addStatement("else -> error(%S)", "Unexpected Substruct")
                        endControlFlow()
                    })
                })
            })
        }
    }
}

////////////////////////////////////////
