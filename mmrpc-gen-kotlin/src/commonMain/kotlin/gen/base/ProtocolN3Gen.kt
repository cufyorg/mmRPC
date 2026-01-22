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
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.FLOW
import org.cufy.mmrpc.gen.kotlin.util.funSpec
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val BASE_NAME = Names.N3

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolN3Gen() {
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
    val routines = element.routines.filter { it.comm == Comms.N3 }

    if (routines.isEmpty())
        return

    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfBaseClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)

            // abstract N3 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.ABSTRACT)
                    addKdoc(createKdocCode(routine))
                    addParameter("request", routine.input.className())
                    returns(FLOW.parameterizedBy(routine.output.className()))
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(interfaceSpec(element.nameOfBaseStubClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(element.generatedBaseClassName(BASE_NAME))
            addProperty(propertySpec("engine", ClientEngine.N3::class))

            // impl N3 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.OVERRIDE)
                    addParameter("request", routine.input.className())
                    returns(FLOW.parameterizedBy(routine.output.className()))

                    addCode(routine.clientStubImplCode(
                        engine = CodeBlock.of("this.engine"),
                        exec = Intrinsics.EXEC3,
                    ))
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.N3.register3( impl: <n3-protocol> )
        addFunction(funSpec("register3") {
            contextParameter("_", ServerEngine.N3::class)
            addParameter("impl", element.generatedBaseClassName(BASE_NAME))

            for (routine in routines) {
                addCode(routine.serverRegisterCode(
                    register = Intrinsics.REGISTER3,
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
        // <protocol>.Companion.<routine>.invoke( handler: WrapHandler3<request> )
        for (routine in routines) {
            addFunction(funSpec(routine.nameOfFunction()) {
                contextParameter("engine", ServerEngine::class)
                receiver(element.generatedClassName().nestedClass("Companion"))
                addParameter(
                    "handler",
                    Intrinsics.WRAP_BLOCK3
                        .parameterizedBy(
                            routine.input.className(),
                            FLOW.parameterizedBy(routine.output.className()),
                        )
                )

                addCode(routine.serverDirectRegisterCode(
                    condition = CodeBlock.of("engine is %T", ServerEngine.N3::class),
                    register = Intrinsics.REGISTER3,
                    wrap = Intrinsics.WRAP3,
                    handler = CodeBlock.of("handler"),
                ))
            })
        }
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <n3-protocol>.<routine>(<request-fields>): <response>
        for (routine in routines) {
            val fields = routine.input.collectAllFields().toList()

            addFunction(funSpec(routine.nameOfFunction()) {
                receiver(element.generatedBaseClassName(BASE_NAME))
                for (field in fields) {
                    addParameter(
                        field.nameOfProperty(),
                        field.type.typeName(),
                    )
                }
                returns(FLOW.parameterizedBy(routine.output.className()))

                addCode(routine.clientFlatImplCode(
                    request = routine.input.className(),
                    fields = fields.map { it.nameOfProperty() },
                ))
            })
        }
    }
}
