package org.cufy.mmrpc.gen.kotlin.context

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging

data class Context(
    val elements: List<ElementDefinition>,

    //
    val packageName: String?,
    val packaging: GenPackaging,
    val features: Set<GenFeature>,

    // names
    val classNames: Map<CanonicalName, String>,

    // scalar classes
    val defaultScalarClass: ClassName?,
    val scalarClasses: Map<CanonicalName, ClassName>,

    // native classes
    val nativeScalarClasses: Map<CanonicalName, ClassName>,
    val nativeMetadataClasses: Map<CanonicalName, ClassName>,

    // userdefined classes
    val userdefinedScalarClasses: Map<CanonicalName, ClassName>,
    val userdefinedMetadataClasses: Map<CanonicalName, ClassName>,
) {
    /**
     * All the elements each associated with itself as a namespace.
     */
    val elementsMap = elements.associateBy { it.canonicalName }

    /**
     * The namespaces of all the elements excluding namespaces
     * that representing elements.
     */
    val roots = elements.asSequence()
        .flatMap { it.namespace?.collect().orEmpty() }
        .minus(elementsMap.keys)
        .toSet()
}
