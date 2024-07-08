package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.ProtocolInfo
import org.cufy.mmrpc.ProtocolObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asClassName
import org.cufy.mmrpc.gen.kotlin.util.fStaticInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.*

class ProtocolDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is ProtocolDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: ProtocolDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: ProtocolDefinition): TypeSpec.Builder {
        val overrideObjectClass = ProtocolObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", ProtocolInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: ProtocolDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, ProtocolInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
