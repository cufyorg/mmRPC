package org.cufy.mmrpc.gen.kotlin.gen.integ

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.experimental.isKafkaSupported
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ServerEngine

private const val INTEG_NAME = "Kafka"

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolKafkaGen() {
    if (GenFeature.INTEG_KAFKA !in ctx.features)
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
        .filter { it.comm.isKafkaSupported() }
        .ifEmpty { return }
    val (refluxRoutines, regularRoutines) = routines
        .partition { it.comm == Comm.VoidUnary }

    inject<TypeSpec.Builder>(target = element.canonicalName) {
        addSuperinterface(element.generatedIntegClassName(INTEG_NAME))
    }
    inject<TypeSpec.Builder>(target = element.refluxCanonicalName) {
        addSuperinterface(element.generatedIntegRefluxClassName(INTEG_NAME))
    }
    declareType(target = element.namespace) {
        interfaceSpec(element.nameOfIntegClass(INTEG_NAME)) {
            for (routine in regularRoutines) {
                addFunction(routine.abstractFunSpec())
            }

            addType(interfaceSpec(NAME_OF_REFLUX_CLASS) {
                for (routine in refluxRoutines) {
                    addFunction(routine.abstractFunSpec())
                }
            })
        }
    }
    toplevel(target = element.namespace, name = element.nameOfStubFile()) {
        addType(classSpec(element.nameOfIntegStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegClassName(INTEG_NAME))

            primaryConstructor(constructorSpec {
                addParameter("engine", ClientEngine.Kafka::class)
            })
            addProperty(propertySpec("engine", ClientEngine.Kafka::class) {
                addModifiers(KModifier.PRIVATE)
                initializer("engine")
            })

            for (routine in regularRoutines) {
                addFunction(routine.clientExecImplFunSpec())
            }
        })
        addType(classSpec(element.nameOfIntegRefluxStubClass(INTEG_NAME)) {
            addSuperinterface(element.generatedIntegRefluxClassName(INTEG_NAME))

            primaryConstructor(constructorSpec {
                addParameter("engine", ClientEngine.Kafka::class)
            })
            addProperty(propertySpec("engine", ClientEngine.Kafka::class) {
                addModifiers(KModifier.PRIVATE)
                initializer("engine")
            })

            for (routine in refluxRoutines) {
                addFunction(routine.clientExecImplFunSpec())
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // ServerEngine.Kafka.register( impl: <Kafka*Impl> )
        addFunction(funSpec("register") {
            contextParameter("_", ServerEngine.Kafka::class)
            addParameter("impl", element.generatedIntegClassName(INTEG_NAME))

            for (routine in regularRoutines) {
                addStatement("%L", routine.serverRegisterImplCode(
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
        // ServerEngine.Kafka.register( impl: <Kafka*RefluxImpl> )
        addFunction(funSpec("register") {
            contextParameter("_", ServerEngine.Kafka::class)
            addParameter("impl", element.generatedIntegRefluxClassName(INTEG_NAME))

            for (routine in refluxRoutines) {
                addStatement("%L", routine.serverRegisterImplCode(
                    handler = CodeBlock.of("impl::%L", routine.nameOfFunction()),
                ))
            }
        })
    }
    toplevel(target = element.namespace, name = element.nameOfClientExtFile()) {
        // <protocol>.Companion.invoke( engine: ClientEngine.Kafka ): Kafka<protocol>
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(
                element.generatedClassName()
                    .nestedClass("Companion")
            )
            addParameter("engine", ClientEngine.Kafka::class)
            returns(element.generatedIntegClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegStubClassName(INTEG_NAME),
            )
        })
        // <protocol>.Reflux.Companion.invoke( engine: ClientEngine.Kafka ): Kafka<protocol>.Reflux
        addFunction(funSpec("invoke") {
            addModifiers(KModifier.OPERATOR)
            receiver(
                element.generatedRefluxClassName()
                    .nestedClass("Companion")
            )
            addParameter("engine", ClientEngine.Kafka::class)
            returns(element.generatedIntegRefluxClassName(INTEG_NAME))
            addStatement(
                "♢return %T(engine)",
                element.generatedIntegRefluxStubClassName(INTEG_NAME),
            )
        })
        // Kafka<protocol>.<routine>(<request-fields>): <response>
        for (routine in regularRoutines) {
            addFunction(routine.clientFlatInputExecFunSpec(
                receiver = element.generatedIntegClassName(INTEG_NAME)
            ))
        }
        // Kafka<protocol>.Reflux.<routine>(<request-fields>): <response>
        for (routine in refluxRoutines) {
            addFunction(routine.clientFlatInputExecFunSpec(
                receiver = element.generatedIntegRefluxClassName(INTEG_NAME)
            ))
        }
    }
}
