package org.cufy.mmrpc.gen.kotlin.gen.base

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.Comms
import org.cufy.mmrpc.gen.kotlin.Intrinsics
import org.cufy.mmrpc.gen.kotlin.Names
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.FLOW
import org.cufy.mmrpc.gen.kotlin.util.funSpec
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val BASE_NAME = Names.N2

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolN2Gen() {
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
    val routines = element.routines.filter { it.comm == Comms.N2 }

    if (routines.isEmpty())
        return

    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfBaseClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)

            // abstract N2 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                    addKdoc(createKdocCode(routine))
                    addParameter("request", FLOW.parameterizedBy(routine.input.className()))
                    returns(routine.output.className())
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(interfaceSpec(element.nameOfBaseStubClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(element.generatedBaseClassName(BASE_NAME))
            addProperty(propertySpec("engine", ClientEngine.N2::class))

            // impl N2 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    addParameter("request", FLOW.parameterizedBy(routine.input.className()))
                    returns(routine.output.className())

                    addCode(routine.clientStubImplCode(
                        engine = CodeBlock.of("this.engine"),
                        exec = Intrinsics.EXEC2,
                    ))
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.N2.register2( impl: <n2-protocol> )
        addFunction(funSpec("register2") {
            contextParameter("_", ServerEngine.N2::class)
            addParameter("impl", element.generatedBaseClassName(BASE_NAME))

            for (routine in routines) {
                addCode(routine.serverRegisterCode(
                    register = Intrinsics.REGISTER2,
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
        // <protocol>.Companion.<routine>.invoke( handler: WrapHandler2<request> )
        for (routine in routines) {
            addFunction(funSpec(routine.nameOfFunction()) {
                contextParameter("engine", ServerEngine::class)
                receiver(element.generatedClassName().nestedClass("Companion"))
                addParameter(
                    "handler",
                    Intrinsics.WRAP_BLOCK2
                        .parameterizedBy(
                            FLOW.parameterizedBy(routine.input.className()),
                            routine.output.className(),
                        )
                )

                addCode(routine.serverDirectRegisterCode(
                    condition = CodeBlock.of("engine is %T", ServerEngine.N2::class),
                    register = Intrinsics.REGISTER2,
                    wrap = Intrinsics.WRAP2,
                    handler = CodeBlock.of("handler"),
                ))
            })
        }
    }
}
