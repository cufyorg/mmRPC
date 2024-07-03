package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.KafkaPublicationEndpointDefinition
import org.cufy.specdsl.KafkaPublicationEndpointInfo
import org.cufy.specdsl.KafkaPublicationEndpointObject
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.fStaticTopic
import org.cufy.specdsl.gen.kotlin.util.poet.*

class KafkaPublicationGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is KafkaPublicationEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: KafkaPublicationEndpointDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addProperty(createStaticTopicProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: KafkaPublicationEndpointDefinition): TypeSpec.Builder {
        val overrideObjectClass = KafkaPublicationEndpointObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", KafkaPublicationEndpointInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: KafkaPublicationEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, KafkaPublicationEndpointInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticTopicProperty(element: KafkaPublicationEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticTopic, STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", element.endpointTopic.value)
            .build()
    }
}
