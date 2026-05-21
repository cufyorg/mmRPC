package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.typeNameOf
import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.gen.kotlin.InjectKind.COMPANION
import org.cufy.mmrpc.gen.kotlin.InjectKind.ELEMENT
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*
import org.cufy.mmrpc.runtime.FaultException

context(ctx: Context, _: FailScope, _: InitStage)
fun doFaultDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is FaultDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addExceptionClass(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addExceptionClass(element: FaultDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        class <name>(message: String? = null, cause: Throwable? = null) :
            FaultException(CANONICAL_NAME, message, cause) {

            constructor(cause: Throwable?) : this(null, cause)

            companion object {
                const val CANONICAL_NAME = "<canonical-name>"
            }
        }
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        classSpec(element.nameOfClass()) {
            superclass(FaultException::class)
            primaryConstructor(constructorSpec {
                addParameter(parameterSpec("message", typeNameOf<String?>()) {
                    defaultValue("null")
                })
                addParameter(parameterSpec("cause", typeNameOf<Throwable?>()) {
                    defaultValue("null")
                })
            })
            addSuperclassConstructorParameter("CANONICAL_NAME")
            addSuperclassConstructorParameter("message")
            addSuperclassConstructorParameter("cause")

            addFunction(constructorSpec {
                addParameter("cause", typeNameOf<Throwable?>())
                callThisConstructor("null", "cause")
            })

            addCompanionObject {
                addProperty(propertySpec("CANONICAL_NAME", STRING) {
                    addModifiers(KModifier.CONST)
                    initializer("%S", element.canonicalName.value)
                })

                applyOf(COMPANION, target = element.canonicalName)
            }

            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(ELEMENT, target = element.canonicalName)
        }
    }
}
