package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.cufy.specdsl.HttpEndpointDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.createKDoc

class HttpGen(override val ctx: GenContext) : GenGroup() {
    fun generateConstants() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is HttpEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generatePathConstant(element)
            }
        }
    }

    private fun generatePathConstant(element: HttpEndpointDefinition) {
        onObject(element.namespace) {
            val propertySpec = PropertySpec
                .builder("${element.name}PATH", STRING)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.endpointPath.value)
                .build()

            addProperty(propertySpec)
        }
    }
}
