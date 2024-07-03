package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.MetadataDefinition
import org.cufy.specdsl.MetadataInfo
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.MetadataStrategy
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.calculateStrategy
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.poet.*

class MetadataDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is MetadataDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    when (calculateStrategy(element)) {
                        MetadataStrategy.DATA_OBJECT
                        -> addType(createDataObject(element))

                        MetadataStrategy.ANNOTATION_CLASS
                        -> addType(createAnnotationClass(element))
                    }
                }
            }
        }
    }

    private fun createAnnotationClass(element: MetadataDefinition): TypeSpec {
        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        val primaryConstructorSpec = FunSpec
            .constructorBuilder()
            .apply {
                for (it in element.metadataParameters) {
                    val parameterSpec = when (val default = it.parameterDefault) {
                        null -> ParameterSpec
                            .builder(it.name, typeOf(it.parameterType))
                            .build()

                        else -> ParameterSpec
                            .builder(it.name, typeOf(it.parameterType))
                            .defaultValue(createLiteralInlinedOrRefOfValue(default))
                            .build()
                    }

                    addParameter(parameterSpec)
                }
            }
            .build()

        return TypeSpec
            .annotationBuilder(element.asClassName)
            .addType(companionObjectSpec)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .primaryConstructor(primaryConstructorSpec)
            .apply {
                for (it in element.metadataParameters) {
                    val propertySpec = PropertySpec
                        .builder(it.name, typeOf(it.parameterType))
                        .addKdoc("@see %L", createKDocReference(it))
                        .addAnnotations(createAnnotationSet(it.metadata))
                        .initializer(it.name)
                        .build()

                    addProperty(propertySpec)
                }
            }
            .build()
    }

    private fun createDataObject(element: MetadataDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addProperty(createStaticInfoProperty(element))
            .addModifiers(KModifier.DATA)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .build()
    }

    private fun createStaticInfoProperty(element: MetadataDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, MetadataInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
