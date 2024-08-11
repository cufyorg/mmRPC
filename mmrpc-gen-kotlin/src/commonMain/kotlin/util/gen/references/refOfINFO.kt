package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ElementInfo
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_INFO
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRequireGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createInfo

private const val TAG = "refOfINFO"

/**
 * Assuming the given [element] has a field containing its [ElementInfo],
 * return a code expression referencing said field.
 */
@Marker3
fun GenGroup.refOfINFO(element: ElementDefinition): CodeBlock {
    debugRequireGeneratedClass(TAG, element)
    return CodeBlock.of("%T.%L", generatedClassOf(element), F_STATIC_INFO)
}

/**
 * If the given [element] has a field containing its [ElementInfo],
 * return a code expression referencing said field.
 * Otherwise, return a code expression creating the [ElementInfo] of [element].
 */
@Marker3
fun GenGroup.refOfINFOOrCreateInfo(element: ElementDefinition): CodeBlock {
    return when {
        hasGeneratedClass(element) -> refOfINFO(element)
        else -> createInfo(element)
    }
}
