package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.StructDefinition
import org.cufy.specdsl.StructInfo
import org.cufy.specdsl.StructObject
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.StructStrategy
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.calculateStrategy
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.poet.*

class StructDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is StructDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    when (calculateStrategy(element)) {
                        StructStrategy.DATA_CLASS
                        -> addType(createDataClass(element))

                        StructStrategy.DATA_OBJECT
                        -> addType(createDataObject(element))
                    }
                }
            }
        }
    }

    private fun createDataClass(element: StructDefinition): TypeSpec {
        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        val primaryConstructorSpec = FunSpec
            .constructorBuilder()
            .apply {
                for (it in element.structFields) {
                    val parameterSpec = when (val default = it.fieldDefault) {
                        null -> ParameterSpec
                            .builder(it.name, typeOf(it.fieldType))
                            .build()

                        else -> ParameterSpec
                            .builder(it.name, typeOf(it.fieldType))
                            .defaultValue(createLiteralInlinedOrRefOfValue(default))
                            .build()
                    }

                    addParameter(parameterSpec)
                }
            }
            .build()

        return TypeSpec
            .classBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addType(companionObjectSpec)
            .overrideObject(element)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .addSuperinterfaces(calculateUnionInterfaces(element))
            .primaryConstructor(primaryConstructorSpec)
            .apply {
                for (it in element.structFields) {
                    val propertySpec = PropertySpec
                        .builder(it.name, typeOf(it.fieldType))
                        .addKdoc("@see %L", createKDocReference(it))
                        .addAnnotations(createAnnotationSet(it.metadata))
                        // fixme nameOrReferenceOf to account for anonymous fields
                        .addAnnotations(createOptionalSerialNameAnnotationSet(refOfName(it)))
                        .initializer("%L", it.name)
                        .build()

                    addProperty(propertySpec)
                }
            }
            .build()
    }

    private fun createDataObject(element: StructDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .overrideObject(element)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .addSuperinterfaces(calculateUnionInterfaces(element))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: StructDefinition): TypeSpec.Builder {
        val overrideObjectClass = StructObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", StructInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: StructDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, StructInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
