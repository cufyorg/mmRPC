package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.ConstObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_VALUE
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.isCompileConst
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class ConstDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is ConstDefinition) continue
            if (!hasGeneratedClass(element)) continue

            failGenBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: ConstDefinition) {
        val superinterface = ConstObject::class.asClassName()
            .parameterizedBy(typeOf(element.constType))

        val staticValue = propertySpec(F_STATIC_VALUE, typeOf(element.constType)) {
            if (isCompileConst(element.constType))
                addModifiers(KModifier.CONST)

            initializer(createLiteral(element))
        }
        val overrideValue = propertySpec("value", typeOf(element.constType)) {
            addModifiers(KModifier.OVERRIDE)
            initializer("%L", F_STATIC_VALUE)
        }

        createObject(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addProperty(staticValue)
            addProperty(overrideValue)
            addProperty(createStaticInfoProperty(element))
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
