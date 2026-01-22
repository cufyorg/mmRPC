package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

const val NAME_OF_REFLUX_CLASS = "Reflux"

val ProtocolDefinition.refluxCanonicalName
    get() = canonicalName + "-reflux"

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfMainFile(): String {
    return $$"$${nameOfClass()}$Main"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfStubFile(): String {
    return $$"$${nameOfClass()}$Stub"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfClientExtFile(): String {
    return $$"$${nameOfClass()}$Client"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfServerExtFile(): String {
    return $$"$${nameOfClass()}$Server"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedRefluxClassName(): ClassName {
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames + "Reflux")
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfBaseClass(baseName: String): String {
    return "_${nameOfClass()}_${baseName}"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfBaseStubClass(baseName: String): String {
    return "_${nameOfClass()}_${baseName}Stub"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedBaseClassName(baseName: String): ClassName {
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames.dropLast(1) + nameOfBaseClass(baseName))
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedBaseStubClassName(baseName: String): ClassName {
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames.dropLast(1) + nameOfBaseStubClass(baseName))
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfIntegClass(integName: String): String {
    return "${integName}${nameOfClass()}"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.nameOfIntegStubClass(integName: String): String {
    return "${integName}${nameOfClass()}Stub"
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedIntegClassName(integName: String): ClassName {
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames.dropLast(1) + nameOfIntegClass(integName))
}

@ContextScope
context(ctx: Context)
fun ProtocolDefinition.generatedIntegStubClassName(integName: String): ClassName {
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames.dropLast(1) + nameOfIntegStubClass(integName))
}

////////////////////////////////////////
