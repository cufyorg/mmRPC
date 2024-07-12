package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_INFO
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRequireGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createInfo

private const val TAG = "refOfINFO"

@Marker3
fun GenGroup.refOfINFO(element: ElementDefinition): CodeBlock {
    debugRequireGeneratedClass(TAG, element)
    return CodeBlock.of("%T.%L", generatedClassOf(element), F_STATIC_INFO)
}

@Marker3
fun GenGroup.refOfINFOOrCreateInfo(element: ElementDefinition): CodeBlock {
    return when {
        hasGeneratedClass(element) -> refOfINFO(element)
        else -> createInfo(element)
    }
}
