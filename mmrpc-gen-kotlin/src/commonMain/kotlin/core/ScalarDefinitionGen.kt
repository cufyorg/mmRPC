package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.ScalarInfo
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.ScalarStrategy
import org.cufy.mmrpc.gen.kotlin.util.asClassName
import org.cufy.mmrpc.gen.kotlin.util.calculateStrategy
import org.cufy.mmrpc.gen.kotlin.util.fStaticInfo
import org.cufy.mmrpc.gen.kotlin.util.poet.*

class ScalarDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is ScalarDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    when (calculateStrategy(element)) {
                        ScalarStrategy.DATA_OBJECT
                        -> addType(createDataObject(element))

                        ScalarStrategy.VALUE_CLASS
                        -> addType(createValueClass(element))
                    }
                }
            }
        }
    }

    private fun createValueClass(element: ScalarDefinition): TypeSpec {
        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        val primaryConstructorSpec = FunSpec
            .constructorBuilder()
            .addParameter("value", nativeClassOf(element))
            .build()

        val valuePropertySpec = PropertySpec
            .builder("value", nativeClassOf(element))
            .initializer("value")
            .build()

        return TypeSpec
            .classBuilder(element.asClassName)
            .addModifiers(KModifier.VALUE)
            .addAnnotation(JvmInline::class)
            .addType(companionObjectSpec)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .addSuperinterfaces(calculateUnionInterfaces(element))
            .primaryConstructor(primaryConstructorSpec)
            .addProperty(valuePropertySpec)
            .build()
    }

    private fun createDataObject(element: ScalarDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun createStaticInfoProperty(element: ScalarDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, ScalarInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
