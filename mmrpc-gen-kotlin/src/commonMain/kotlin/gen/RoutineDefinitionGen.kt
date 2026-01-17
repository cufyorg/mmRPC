package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.objectSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doRoutineDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is RoutineDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addDataObject(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addDataObject(element: RoutineDefinition) {
    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        objectSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            addProperty(propertySpec("CANONICAL_NAME", STRING) {
                addModifiers(KModifier.CONST)
                initializer("%S", element.canonicalName.value)
            })

            applyOf(target = element.canonicalName)
        }
    }
}
