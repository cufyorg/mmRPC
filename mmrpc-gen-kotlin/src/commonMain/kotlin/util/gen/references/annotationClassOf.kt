package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRejectAnonymous
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative

private const val TAG = "annotationClassOf"

@Marker3
fun GenGroup.annotationClassOf(element: MetadataDefinition): ClassName {
    debugRejectAnonymous(TAG, element)
    return when {
        isNative(element) -> nativeClassOf(element)
        else -> generatedClassOf(element)
    }
}
