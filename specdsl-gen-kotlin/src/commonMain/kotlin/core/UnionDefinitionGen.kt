package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.specdsl.ConstDefinition
import org.cufy.specdsl.UnionDefinition
import org.cufy.specdsl.UnionInfo
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.UnionStrategy
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.calculateStrategy
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.poet.*

class UnionDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is UnionDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    when (calculateStrategy(element)) {
                        UnionStrategy.SEALED_CLASS
                        -> addType(createSealedClass(element))

                        UnionStrategy.SEALED_INTERFACE
                        -> addType(createSealedInterface(element))

                        UnionStrategy.ENUM_CLASS
                        -> addType(createEnumClass(element))

                        UnionStrategy.DATA_OBJECT
                        -> addType(createDataObject(element))
                    }
                }
            }
        }
    }

    private fun createSealedClass(element: UnionDefinition): TypeSpec {
        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        return TypeSpec
            .classBuilder(element.asClassName)
            .addModifiers(KModifier.SEALED)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .addSuperinterfaces(calculateUnionInterfaces(element))
            .addType(companionObjectSpec)
            .apply {
                for (it in element.unionTypes) {
                    val primaryConstructorSpec = FunSpec
                        .constructorBuilder()
                        .addParameter("value", typeOf(it))
                        .build()

                    val valuePropertySpec = PropertySpec
                        .builder("value", typeOf(it))
                        .initializer("value")
                        .build()

                    val typeSpec = TypeSpec
                        .classBuilder(it.name)
                        .addModifiers(KModifier.VALUE)
                        .addAnnotation(JvmInline::class)
                        .superclass(classOf(element))
                        .addKdoc("@see %L", createKDocReference(it))
                        .addAnnotations(createAnnotationSet(it.metadata))
                        .addAnnotations(createOptionalSerializableAnnotationSet())
                        .addAnnotations(createOptionalSerialNameAnnotationSet(it.canonicalName.value))
                        .primaryConstructor(primaryConstructorSpec)
                        .addProperty(valuePropertySpec)
                        .build()

                    addType(typeSpec)
                }
            }
            .build()
    }

    private fun createSealedInterface(element: UnionDefinition): TypeSpec {
        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        return TypeSpec
            .interfaceBuilder(element.asClassName)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .addModifiers(KModifier.SEALED)
            .addSuperinterfaces(calculateUnionInterfaces(element))
            .addType(companionObjectSpec)
            .build()
    }

    private fun createEnumClass(element: UnionDefinition): TypeSpec {
        @Suppress("UNCHECKED_CAST")
        val unionTypes = element.unionTypes as List<ConstDefinition>
        val commonType = unionTypes[0].constType

        val companionObjectSpec = TypeSpec
            .companionObjectBuilder()
            .addProperty(createStaticInfoProperty(element))
            .build()

        val primaryConstructorSpec = FunSpec
            .constructorBuilder()
            .addParameter("value", typeOf(commonType))
            .build()

        val valuePropertySpec = PropertySpec
            .builder("value", typeOf(commonType))
            .initializer("value")
            .build()

        return TypeSpec
            .enumBuilder(element.asClassName)
            .addType(companionObjectSpec)
            .addKdoc("@see %L", createKDocReference(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .primaryConstructor(primaryConstructorSpec)
            .addProperty(valuePropertySpec)
            .apply {
                for (it in unionTypes) {
                    val typeSpec = TypeSpec
                        .anonymousClassBuilder()
                        .addKdoc("@see %L", createKDocReference(it))
                        .addAnnotations(createAnnotationSet(it.metadata))
                        // TODO is this ok? why not element.canonicalName.value?
                        .addAnnotations(createOptionalSerialNameAnnotationSet(createLiteralInlinedOrRefOfValue(it)))
                        .addSuperclassConstructorParameter(createLiteralOrRefOfValue(it))
                        .build()

                    addEnumConstant(it.name, typeSpec)
                }
            }
            .build()
    }

    private fun createDataObject(element: UnionDefinition): TypeSpec {
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

    private fun createStaticInfoProperty(element: UnionDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, UnionInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }
}
