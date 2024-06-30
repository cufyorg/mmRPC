package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.*

private const val TAG = "referenceOf"

@Marker0
fun GenGroup.referenceOf(element: Namespace): CodeBlock {
    return CodeBlock.of("%L", element.asQualifiedReferenceName)
}

@Marker0
fun GenGroup.referenceOf(element: ConstDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)
    debugRejectNative(TAG, element)

    return CodeBlock.of("%L.%L", element.namespace.asClassName, element.asReferenceName)
}

@Marker0
fun GenGroup.referenceOf(element: FaultDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)

    return CodeBlock.of("%L.%L", element.namespace.asClassName, element.asReferenceName)
}

@Marker0
fun GenGroup.referenceOf(element: FieldDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)

    return CodeBlock.of("%L.%L", element.namespace.asClassName, element.asReferenceName)
}
