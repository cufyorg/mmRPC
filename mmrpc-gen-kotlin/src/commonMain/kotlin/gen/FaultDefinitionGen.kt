package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.FaultObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.asClassName
import org.cufy.mmrpc.gen.kotlin.common.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.common.createKDoc
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class FaultDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is FaultDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: FaultDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            data object <name> : FaultObject {
                const val CANONICAL_NAME = "<canonical-name>"

                override val canonicalName = CanonicalName(CANONICAL_NAME)
            }
        }
         */

        createType(element.canonicalName) {
            objectBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)
                addSuperinterface(FaultObject::class)

                addProperty(propertySpec("CANONICAL_NAME", STRING) {
                    addModifiers(KModifier.CONST)
                    initializer("%S", element.canonicalName.value)
                })
                addProperty(propertySpec("canonicalName", CanonicalName::class) {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("%T(CANONICAL_NAME)", CanonicalName::class)
                })

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            }
        }
    }
}
