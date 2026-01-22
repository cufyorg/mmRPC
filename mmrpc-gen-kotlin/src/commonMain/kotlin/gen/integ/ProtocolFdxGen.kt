package org.cufy.mmrpc.gen.kotlin.gen.integ

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.Comms
import org.cufy.mmrpc.gen.kotlin.Names
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*
import org.cufy.mmrpc.runtime.FdxClientEngine
import org.cufy.mmrpc.runtime.FdxServerEngine

private const val INTEG_NAME = "Fdx"

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolFdxGen() {
    for (element in ctx.elements) {
        if (element !is ProtocolDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            apply(element)
        }
    }
}

@OptIn(ExperimentalKotlinPoetApi::class)
context(ctx: Context, _: InitStage)
private fun apply(element: ProtocolDefinition) {
    val n0 = element.routines.any { it.comm == Comms.N0 }
    val n1 = element.routines.any { it.comm == Comms.N1 }
    val n2 = element.routines.any { it.comm == Comms.N2 }
    val n3 = element.routines.any { it.comm == Comms.N3 }
    val n4 = element.routines.any { it.comm == Comms.N4 }

    if (!(n0 || n1 || n2 || n3 || n4))
        return

    inject<TypeSpec.Builder>(target = element.canonicalName) {
        addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
    }
    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfIntegClass(INTEG_NAME)) {
            if (n0) addSuperinterface(element.generatedBaseClassName(Names.N0))
            if (n1) addSuperinterface(element.generatedBaseClassName(Names.N1))
            if (n2) addSuperinterface(element.generatedBaseClassName(Names.N2))
            if (n3) addSuperinterface(element.generatedBaseClassName(Names.N3))
            if (n4) addSuperinterface(element.generatedBaseClassName(Names.N4))
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(classSpec(element.nameOfIntegStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
            if (n0) addSuperinterface(element.generatedBaseStubClassName(Names.N0))
            if (n1) addSuperinterface(element.generatedBaseStubClassName(Names.N1))
            if (n2) addSuperinterface(element.generatedBaseStubClassName(Names.N2))
            if (n3) addSuperinterface(element.generatedBaseStubClassName(Names.N3))
            if (n4) addSuperinterface(element.generatedBaseStubClassName(Names.N4))

            primaryConstructor(constructorSpec {
                addParameter("engine", FdxClientEngine::class)
            })
            addProperty(propertySpec("engine", FdxClientEngine::class) {
                addModifiers(KModifier.OVERRIDE)
                initializer("engine")
            })
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // FdxServerEngine.register( impl: <fdx-protocol> )
        addFunction(funSpec("register") {
            contextParameter("_", FdxServerEngine::class)
            addParameter("impl", element.generatedIntegClassName(INTEG_NAME))
            if (n0) addStatement("register0(impl)")
            if (n1) addStatement("register1(impl)")
            if (n2) addStatement("register2(impl)")
            if (n3) addStatement("register3(impl)")
            if (n4) addStatement("register4(impl)")
        })
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <protocol>.Companion.invoke( engine: FdxClientEngine ): <fdx-protocol>
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(element.generatedClassName().nestedClass("Companion"))
            addParameter("engine", FdxClientEngine::class)
            returns(element.generatedIntegClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegStubClassName(INTEG_NAME),
            )
        })
    }
}
