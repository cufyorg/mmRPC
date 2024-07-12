package org.cufy.mmrpc.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.KafkaPublicationEndpointDefinition
import org.cufy.mmrpc.KafkaPublicationEndpointObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_TOPIC
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class KafkaPublicationGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is KafkaPublicationEndpointDefinition) continue
            if (!hasGeneratedClass(element)) continue

            failGenBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: KafkaPublicationEndpointDefinition) {
        val superinterface = KafkaPublicationEndpointObject::class.asClassName().let {
            when (val endpointKey = element.endpointKey) {
                null -> it.parameterizedBy(STRING)
                else -> it.parameterizedBy(typeOf(endpointKey))
            }
        }

        val staticTopic = propertySpec(F_STATIC_TOPIC, STRING) {
            addModifiers(KModifier.CONST)
            initializer("%S", element.endpointTopic.value)
        }

        createObject(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addProperty(staticTopic)
            addProperty(createStaticInfoProperty(element))
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
