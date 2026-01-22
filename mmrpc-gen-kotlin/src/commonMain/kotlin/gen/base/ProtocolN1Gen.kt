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
import org.cufy.mmrpc.gen.kotlin.util.funSpec
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val BASE_NAME = Names.N1

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolN1Gen() {
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
    val routines = element.routines.filter { it.comm == Comms.N1 }

    if (routines.isEmpty())
        return

    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        addType(interfaceSpec(element.nameOfBaseClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)

            // abstract N1 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                    addKdoc(createKdocCode(routine))
                    addParameter("request", routine.input.className())
                    returns(routine.output.className())
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(interfaceSpec(element.nameOfBaseStubClass(BASE_NAME)) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(element.generatedBaseClassName(BASE_NAME))
            addProperty(propertySpec("engine", ClientEngine.N1::class))

            // impl N1 functions
            for (routine in routines) {
                addFunction(funSpec(routine.nameOfFunction()) {
                    addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    addParameter("request", routine.input.className())
                    returns(routine.output.className())

                    addCode(routine.clientStubImplCode(
                        engine = CodeBlock.of("this.engine"),
                        exec = Intrinsics.EXEC1,
                    ))
                })
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.N1.register1( impl: <n1-protocol> )
        addFunction(funSpec("register1") {
            contextParameter("_", ServerEngine.N1::class)
            addParameter("impl", element.generatedBaseClassName(BASE_NAME))

            for (routine in routines) {
                addStatement("%L", routine.serverRegisterCode(
                    register = Intrinsics.REGISTER1,
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
        // <protocol>.Companion.<routine>.invoke( handler: WrapHandler1<request> )
        for (routine in routines) {
            addFunction(funSpec(routine.nameOfFunction()) {
                contextParameter("engine", ServerEngine::class)
                receiver(element.generatedClassName().nestedClass("Companion"))
                addParameter(
                    "handler",
                    Intrinsics.WRAP_BLOCK1
                        .parameterizedBy(
                            routine.input.className(),
                            routine.output.className(),
                        )
                )

                addCode(routine.serverDirectRegisterCode(
                    condition = CodeBlock.of("engine is %T", ServerEngine.N1::class),
                    register = Intrinsics.REGISTER1,
                    wrap = Intrinsics.WRAP1,
                    handler = CodeBlock.of("handler"),
                ))
            })
        }
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <n1-protocol>.<routine>(<request-fields>): <response>
        for (routine in routines) {
            val fields = routine.input.collectAllFields().toList()

            addFunction(funSpec(routine.nameOfFunction()) {
                addModifiers(KModifier.SUSPEND)
                receiver(element.generatedBaseClassName(BASE_NAME))
                for (field in fields) {
                    addParameter(field.parameterSpec())
                }
                returns(routine.output.className())

                addCode(routine.clientFlatImplCode(
                    request = routine.input.className(),
                    fields = fields.map { it.nameOfProperty() },
                ))
            })
        }
    }
}
