package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.funSpec

context(ctx: GenContext)
fun consumeRoutineDefinition() {
    for (element in ctx.elements) {
        if (element !is RoutineDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateDataObject(element)
            applyCreateAbstractFunction(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: RoutineDefinition) {
    createType(element.canonicalName) {
        objectBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}

context(ctx: GenContext)
private fun applyCreateAbstractFunction(element: RoutineDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        abstract function <name>(<input-type-base-on-comm-shape>):
                <output-type-base-on-comm-shape>
    }
     */

    injectType(element.namespace!!) {
        addFunction(funSpec(element.name) {
            addModifiers(KModifier.ABSTRACT)

            when (element.comm.input) {
                Comm.Shape.Void -> {}

                Comm.Shape.Unary ->
                    addParameter("input", element.input.typeName())

                Comm.Shape.Stream ->
                    addParameter("input", Flow::class.asClassName().parameterizedBy(element.input.typeName()))
            }
            when (element.comm.output) {
                Comm.Shape.Void -> {}

                Comm.Shape.Unary ->
                    addParameter("output", element.output.typeName())

                Comm.Shape.Stream ->
                    addParameter("output", Flow::class.asClassName().parameterizedBy(element.output.typeName()))
            }
        })
    }
}
