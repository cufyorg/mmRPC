package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.isGeneratingTypealias
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.typealiasSpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doArrayDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is ArrayDefinition) continue
        if (!element.isGeneratingTypealias()) continue

        catch(element) {
            addTypealias(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addTypealias(element: ArrayDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        typealias <name> = List< <class-of-type> >
    }
    */

    val target = LIST.parameterizedBy(element.type.typeName())

    injectOrToplevel<FileSpec.Builder>(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        addTypeAlias(typealiasSpec(element.nameOfClass(), target) {
            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(target = element.canonicalName)
        })
    }
}
