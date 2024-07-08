package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.FieldInfo
import org.cufy.mmrpc.FieldObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asClassName
import org.cufy.mmrpc.gen.kotlin.util.fStaticInfo
import org.cufy.mmrpc.gen.kotlin.util.fStaticName
import org.cufy.mmrpc.gen.kotlin.util.poet.*

class FieldDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is FieldDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: FieldDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addProperty(createStaticNameProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: FieldDefinition): TypeSpec.Builder {
        val overrideObjectClass = FieldObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", FieldInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: FieldDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, FieldInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticNameProperty(element: FieldDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticName, STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", element.name)
            .build()
    }
}
