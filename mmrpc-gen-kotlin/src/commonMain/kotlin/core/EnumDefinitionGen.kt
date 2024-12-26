package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.EnumObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.asEnumEntryName
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDocShort
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.anonymousClassSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class EnumDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is EnumDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failGenBoundary {
                applyCreateEnumClass(element)
            }
        }
    }

    private fun applyCreateEnumClass(element: EnumDefinition) {
        val superinterface = EnumObject::class.asClassName()
            .parameterizedBy(typeOf(element.enumType))

        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val entries = element.enumEntries.associate {
            val valueContentToString = CodeBlock.of(it.constValue.contentToString())

            val itValue = propertySpec("value", typeOf(element.enumType)) {
                addModifiers(KModifier.OVERRIDE)
                initializer(createLiteral(it))
            }

            asEnumEntryName(it) to anonymousClassSpec {
                addProperty(itValue)

                addKdoc(createKDocShort(it))
                addAnnotations(createAnnotationSet(it.metadata))
                addAnnotations(createSerialNameAnnotationSet(valueContentToString))
            }
        }

        createEnum(element) {
            addSuperinterface(superinterface)
            addType(companionObject)
            entries.forEach { (name, type) ->
                addEnumConstant(name, type)
            }
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
