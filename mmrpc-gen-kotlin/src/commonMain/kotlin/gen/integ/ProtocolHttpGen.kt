package org.cufy.mmrpc.gen.kotlin.gen.integ

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.experimental.isHttpSupported
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val INTEG_NAME = "Http"

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolHttpGen() {
    if (GenFeature.INTEG_HTTP !in ctx.features)
        return

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
    val routines = element.routines
        .filter { it.comm.isHttpSupported() }
        .ifEmpty { return }
    val (_, regularRoutines) = routines
        .partition { it.comm == Comm.VoidUnary }

    inject<TypeSpec.Builder>(target = element.canonicalName) {
        addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
    }
    declareType(target = element.namespace) {
        interfaceSpec(element.nameOfIntegClass(INTEG_NAME)) {
            for (routine in regularRoutines) {
                addFunction(routine.abstractFunSpec())
            }
        }
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(classSpec(element.nameOfIntegStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegClassName(INTEG_NAME))

            primaryConstructor(constructorSpec {
                addParameter("engine", ClientEngine.Http::class)
            })
            addProperty(propertySpec("engine", ClientEngine.Http::class) {
                addModifiers(KModifier.PRIVATE)
                initializer("engine")
            })

            for (routine in regularRoutines) {
                addFunction(routine.clientExecImplFunSpec())
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.Http.register( impl: <Http*Impl> )
        addFunction(funSpec("register") {
            contextParameter("_", ServerEngine.Http::class)
            addParameter("impl", element.generatedIntegClassName(INTEG_NAME))

            for (routine in regularRoutines) {
                addStatement("%L", routine.serverRegisterImplCode(
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <protocol>.Companion.invoke( engine: ClientEngine.Http ): Http<protocol>
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(
                element.generatedClassName()
                    .nestedClass("Companion")
            )
            addParameter("engine", ClientEngine.Http::class)
            returns(element.generatedIntegClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegStubClassName(INTEG_NAME),
            )
        })
        // Http<protocol>.<routine>(<request-fields>): <response>
        for (routine in regularRoutines) {
            addFunction(routine.clientFlatInputExecFunSpec(
                receiver = element.generatedIntegClassName(INTEG_NAME)
            ))
        }
    }
}
