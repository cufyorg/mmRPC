package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.createCallSingleVararg
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import kotlin.reflect.KType

data class RoutineDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is RoutineDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: RoutineDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            data object <name> : RoutineObject<I, O> {
                const val CANONICAL_NAME = "<canonical-name>"

                override val canonicalName = CanonicalName(CANONICAL_NAME)
                override val comm = setOf(<comm>)
                override val typeI = typeOf<I>()
                override val typeO = typeOf<O>()
            }
        }
         */

        val superinterface = RoutineObject::class.asClassName()
            .parameterizedBy(
                /* I */ classOf(element.input),
                /* O */ classOf(element.output),
            )

        createType(element.canonicalName) {
            objectBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)
                addSuperinterface(superinterface)

                addProperty(propertySpec("CANONICAL_NAME", STRING) {
                    addModifiers(KModifier.CONST)
                    initializer("%S", element.canonicalName.value)
                })
                addProperty(propertySpec("canonicalName", CanonicalName::class) {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("%T(CANONICAL_NAME)", CanonicalName::class)
                })
                addProperty(propertySpec("comm", SET.parameterizedBy(Comm::class.asTypeName())) {
                    addModifiers(KModifier.OVERRIDE)
                    initializer(
                        createCallSingleVararg(
                            CodeBlock.of("setOf"),
                            element.comm.map {
                                CodeBlock.of("%T(%S)", Comm::class, it.value)
                            }
                        )
                    )
                })
                addProperty(propertySpec("typeI", KType::class) {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("typeOf<%T>", classOf(element.input))
                })
                addProperty(propertySpec("typeO", KType::class) {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("typeOf<%T>", classOf(element.output))
                })

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            }
        }
    }
}
