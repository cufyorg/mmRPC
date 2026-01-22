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
import org.cufy.mmrpc.runtime.HdxClientEngine
import org.cufy.mmrpc.runtime.HdxServerEngine

private const val INTEG_NAME = "Hdx"

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolHdxGen() {
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

    if (!(n0 || n1))
        return

    inject<TypeSpec.Builder>(target = element.canonicalName) {
        addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
    }
    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfIntegClass(INTEG_NAME)) {
            if (n0) addSuperinterface(element.generatedBaseClassName(Names.N0))
            if (n1) addSuperinterface(element.generatedBaseClassName(Names.N1))
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(classSpec(element.nameOfIntegStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
            if (n0) addSuperinterface(element.generatedBaseStubClassName(Names.N0))
            if (n1) addSuperinterface(element.generatedBaseStubClassName(Names.N1))

            primaryConstructor(constructorSpec {
                addParameter("engine", HdxClientEngine::class)
            })
            addProperty(propertySpec("engine", HdxClientEngine::class) {
                addModifiers(KModifier.OVERRIDE)
                initializer("engine")
            })
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // HdxServerEngine.register( impl: <hdx-protocol> )
        addFunction(funSpec("register") {
            contextParameter("_", HdxServerEngine::class)
            addParameter("impl", element.generatedIntegClassName(INTEG_NAME))
            if (n0) addStatement("register0(impl)")
            if (n1) addStatement("register1(impl)")
        })
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <protocol>.Companion.invoke( engine: HdxClientEngine ): <hdx-protocol>
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(element.generatedClassName().nestedClass("Companion"))
            addParameter("engine", HdxClientEngine::class)
            returns(element.generatedIntegClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegStubClassName(INTEG_NAME),
            )
        })
    }
}
