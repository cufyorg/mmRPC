package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.IframeEndpointDefinition
import org.cufy.specdsl.IframeEndpointInfo
import org.cufy.specdsl.IframeEndpointObject
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.fStaticPath
import org.cufy.specdsl.gen.kotlin.util.poet.*

class IframeGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is IframeEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: IframeEndpointDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addProperty(createStaticPathProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: IframeEndpointDefinition): TypeSpec.Builder {
        val overrideObjectClass = IframeEndpointObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", IframeEndpointInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: IframeEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, IframeEndpointInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticPathProperty(element: IframeEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticPath, STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", element.endpointPath.value)
            .build()
    }
}
