package org.cufy.mmrpc.gen.kotlin.gen

import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.NAME_OF_REFLUX_CLASS
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfMainFile
import org.cufy.mmrpc.gen.kotlin.common.model.refluxCanonicalName
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
    toplevel(target = element.namespace, name = element.nameOfMainFile()) {
        // Integ-agnostic interfaces
        addType(interfaceSpec(element.nameOfClass()) {
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
        })
    }
}
