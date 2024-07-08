package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.FaultInfo
import org.cufy.mmrpc.FaultObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asClassName
import org.cufy.mmrpc.gen.kotlin.util.fStaticInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.*

class FaultDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is FaultDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: FaultDefinition): TypeSpec {
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

    private fun TypeSpec.Builder.overrideObject(element: FaultDefinition): TypeSpec.Builder {
        val overrideObjectClass = FaultObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", FaultInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: FaultDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, FaultInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
