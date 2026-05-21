package org.cufy.mmrpc.gen.kotlin.gen.integ

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.Comms
import org.cufy.mmrpc.gen.kotlin.InjectKind.ELEMENT
import org.cufy.mmrpc.gen.kotlin.Names
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*
import org.cufy.mmrpc.runtime.SxClientEngine
import org.cufy.mmrpc.runtime.SxServerEngine

private const val INTEG_NAME = Names.SX

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolSxGen() {
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
    if (element.routines.none { it.comm == Comms.N0 })
        return

    inject<TypeSpec.Builder>(ELEMENT, target = element.canonicalName) {
        addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
    }
    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfIntegClass(INTEG_NAME)) {
            addSuperinterface(element.generatedBaseClassName(Names.N0))
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(classSpec(element.nameOfIntegStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
            addSuperinterface(element.generatedBaseStubClassName(Names.N0))

            primaryConstructor(constructorSpec {
                addParameter("engine", SxClientEngine::class)
            })
            addProperty(propertySpec("engine", SxClientEngine::class) {
                addModifiers(KModifier.OVERRIDE)
                initializer("engine")
            })
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // SxServerEngine.register( impl: <sx-protocol> )
        addFunction(funSpec("register") {
            contextParameter("_", SxServerEngine::class)
            addParameter("impl", element.generatedIntegClassName(INTEG_NAME))
            addCode("register0(impl)")
        })
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <protocol>.Companion.invoke( engine: SxClientEngine ): <sx-protocol>
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(element.generatedClassName().nestedClass("Companion"))
            addParameter("engine", SxClientEngine::class)
            returns(element.generatedIntegClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegStubClassName(INTEG_NAME),
            )
        })
    }
}
