package org.cufy.mmrpc.gen.kotlin.gen

import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doProtocolDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is ProtocolDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addInterface(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addInterface(element: ProtocolDefinition) {
    val routines = element.routines
//        .ifEmpty { return } // keep commented for dev sanity sake
    val (refluxRoutines, regularRoutines) = routines
        .partition { it.comm == Comm.VoidUnary }

    declareType(
        target = element.namespace,
        declares = listOf(
            element.canonicalName,
            element.refluxCanonicalName,
        ),
    ) {
        // Integ-agnostic interfaces
        interfaceSpec(element.nameOfClass()) {
            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            addType(interfaceSpec(NAME_OF_REFLUX_CLASS) {
                applyOf(target = element.refluxCanonicalName)
                addType(companionObjectSpec()) // this must be last for syntax compatibility
            })

            applyOf(target = element.canonicalName)
            addType(companionObjectSpec()) // this must be last for syntax compatibility
        }
    }
    toplevel(target = element.namespace, name = element.nameOfServerExtFile()) {
        // <protocol>.Companion.<routine>.invoke( handler: (Req) -> Res )
        for (routine in regularRoutines) {
            addFunction(routine.serverDirectRegisterFunSpec(
                receiver = element.generatedClassName()
                    .nestedClass("Companion")
            ))
        }

        // <protocol>.Reflux.Companion.<routine>.invoke( handler: (Req) -> Res )
        for (routine in refluxRoutines) {
            addFunction(routine.serverDirectRegisterFunSpec(
                receiver = element.generatedRefluxClassName()
                    .nestedClass("Companion")
            ))
        }
    }
}
