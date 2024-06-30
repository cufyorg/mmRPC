package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.cufy.specdsl.KafkaPublicationEndpointDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.createKDoc

class KafkaPublicationGen(override val ctx: GenContext) : GenGroup() {
    fun generateConstants() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is KafkaPublicationEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generateTopicConstant(element)
            }
        }
    }

    private fun generateTopicConstant(element: KafkaPublicationEndpointDefinition) {
        onObject(element.namespace) {
            val propertySpec = PropertySpec
                .builder("${element.name}TOPIC", STRING)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.endpointTopic.value)
                .build()

            addProperty(propertySpec)
        }
    }
}
