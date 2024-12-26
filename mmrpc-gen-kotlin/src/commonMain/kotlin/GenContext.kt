package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Namespace

class GenContext(
    val elements: Set<ElementDefinition>,
    val ignore: Set<CanonicalName>,

    //
    val packageName: String,
    val packaging: GenPackaging,
    val features: Set<GenFeature>,

    // names
    val classNames: Map<CanonicalName, String>,
    val protocolSuffix: String,

    // scalar classes
    val defaultScalarClass: ClassName?,
    val scalarClasses: Map<CanonicalName, ClassName>,

    // native classes
    val nativeScalarClasses: Map<CanonicalName, ClassName>,
    val nativeMetadataClasses: Map<CanonicalName, ClassName>,
    val nativeConstants: Set<CanonicalName>,

    // userdefined classes
    val userdefinedScalarClasses: Map<CanonicalName, ClassName>,
    val userdefinedMetadataClasses: Map<CanonicalName, ClassName>,
) : GenScope() {
    override val ctx: GenContext = this
    override fun apply() {}

    /**
     * All the elements each associated with itself as a namespace.
     */
    val elementsNS = elements.associateBy { it.asNamespace }

    /**
     * The namespaces of all the elements excluding namespaces
     * that representing elements.
     */
    val rootsNS = elements.asSequence()
        .filterNot { it.isAnonymous }
        .flatMap { it.namespace.collect() }
        .minus(elementsNS.keys)
        .plus(Namespace.Toplevel)
        .toSet()

    val failures = mutableListOf<GenException>()

    val createElementNodes = mutableListOf<CreateElementNode>()

    val onElementNodes = mutableListOf<OnElementNode>()
}
