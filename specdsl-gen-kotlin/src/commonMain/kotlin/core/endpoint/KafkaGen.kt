package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.KafkaEndpointDefinition
import org.cufy.specdsl.KafkaEndpointInfo
import org.cufy.specdsl.KafkaEndpointObject
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.fStaticTopic
import org.cufy.specdsl.gen.kotlin.util.poet.*

class KafkaGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is KafkaEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: KafkaEndpointDefinition): TypeSpec {
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

    private fun TypeSpec.Builder.overrideObject(element: KafkaEndpointDefinition): TypeSpec.Builder {
        val overrideObjectClass = KafkaEndpointObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", KafkaEndpointInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: KafkaEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, KafkaEndpointInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticTopicProperty(element: KafkaEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticTopic, STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", element.endpointTopic.value)
            .build()
    }
}
