package org.cufy.mmrpc.gen.kotlin.gen.base

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.UNIT
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.Comms
import org.cufy.mmrpc.gen.kotlin.Intrinsics
import org.cufy.mmrpc.gen.kotlin.Names
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.funSpec
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val BASE_NAME = Names.N0

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolN0Gen() {
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
    val routines = element.routines.filter { it.comm == Comms.N0 }

    if (routines.isEmpty())
        return

    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfBaseClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)

            // abstract N0 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                    addKdoc(createKdocCode(routine))
                    addParameter("request", routine.input.className())
                    returns(UNIT)
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(interfaceSpec(element.nameOfBaseStubClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(element.generatedBaseClassName(BASE_NAME))
            addProperty(propertySpec("engine", ClientEngine.N0::class))

            // impl N0 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    addParameter("request", routine.input.className())
                    returns(UNIT)

                    addCode(routine.clientStubImplCode(
                        engine = CodeBlock.of("this.engine"),
                        exec = Intrinsics.EXEC0,
                    ))
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.N0.register0( impl: <n0-protocol> )
        addFunction(funSpec("register0") {
            contextParameter("_", ServerEngine.N0::class)
            addParameter("impl", element.generatedBaseClassName(BASE_NAME))

            for (routine in routines) {
                addStatement("%L", routine.serverRegisterCode(
                    register = Intrinsics.REGISTER0,
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
        // <protocol>.Companion.<routine>.invoke( handler: WrapHandler0<request> )
        for (routine in routines) {
            addFunction(funSpec(routine.nameOfFunction()) {
                contextParameter("engine", ServerEngine::class)
                receiver(element.generatedClassName().nestedClass("Companion"))
                addParameter(
                    "handler",
                    Intrinsics.WRAP_BLOCK0
                        .parameterizedBy(routine.input.className())
                )

                addCode(routine.serverDirectRegisterCode(
                    condition = CodeBlock.of("engine is %T", ServerEngine.N0::class),
                    register = Intrinsics.REGISTER0,
                    wrap = Intrinsics.WRAP0,
                    handler = CodeBlock.of("handler"),
                ))
            })
        }
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <n0-protocol>.<routine>(<request-fields>): <response>
        for (routine in routines) {
            val fields = routine.input.collectAllFields().toList()

            addFunction(funSpec(routine.nameOfFunction()) {
                addModifiers(KModifier.SUSPEND)
                receiver(element.generatedBaseClassName(BASE_NAME))
                for (field in fields) {
                    addParameter(
                        field.nameOfProperty(),
                        field.type.typeName(),
                    )
                }
                returns(UNIT)

                addCode(routine.clientFlatImplCode(
                    request = routine.input.className(),
                    fields = fields.map { it.nameOfProperty() },
                ))
            })
        }
    }
}
