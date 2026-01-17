package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberSpecHolder
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.code.createLiteralCode
import org.cufy.mmrpc.gen.kotlin.common.isCompileConst
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.isGeneratingProperty
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfProperty
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doConstDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is ConstDefinition) continue
        if (!element.isGeneratingProperty()) continue

        catch(element) {
            addProperty(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addProperty(element: ConstDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        const val <name> = <value>
    }
    */

    injectOrToplevel<MemberSpecHolder.Builder<*>>(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        addProperty(propertySpec(element.nameOfProperty(), element.type.typeName()) {
            if (element.type.isCompileConst()) {
                addModifiers(KModifier.CONST)
            }

            initializer(createLiteralCode(element.type, element.value))

            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(target = element.canonicalName)
        })
    }
}
