package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative
import org.cufy.mmrpc.gen.kotlin.util.gen.isUserdefined

private const val TAG = "annotationClassOf"

@Marker3
fun GenScope.annotationClassOf(element: MetadataDefinition): ClassName {
    return when {
        isNative(element) -> nativeClassOf(element)
        isUserdefined(element) -> userdefinedClassOf(element)
        hasGeneratedClass(element) -> generatedClassOf(element)
        else -> failGen(TAG, element) { "Cannot determine annotation class of element." }
    }
}
