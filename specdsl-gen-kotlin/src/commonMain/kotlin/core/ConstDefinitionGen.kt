package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.fStaticValue
import org.cufy.specdsl.gen.kotlin.util.poet.*

class ConstDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is ConstDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: ConstDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addProperty(createStaticValueProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: ConstDefinition): TypeSpec.Builder {
        val overrideObjectClass = ConstObject::class.asClassName()
            .parameterizedBy(typeOf(element))

        val infoPropertySpec = PropertySpec
            .builder("info", ConstInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        val valuePropertySpec = PropertySpec
            .builder("value", typeOf(element))
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticValue)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
            .addProperty(valuePropertySpec)
    }

    private fun createStaticInfoProperty(element: ConstDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, ConstInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticValueProperty(element: ConstDefinition): PropertySpec {
        val isCompileTimeConstant = when {
            element.constType !is ScalarDefinition -> false
            element.constType.canonicalName !in ctx.nativeElements -> false
            element.constValue is TupleLiteral -> false
            else -> true
        }

        return PropertySpec
            .builder(element.fStaticValue, typeOf(element))
            .apply {
                if (isCompileTimeConstant)
                    addModifiers(KModifier.CONST)
            }
            .initializer(createLiteral(element))
            .build()
    }
}
