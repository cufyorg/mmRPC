package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition

class GenContext(
    val elements: List<ElementDefinition>,
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
    val elementsMap = elements.associateBy { it.canonicalName }

    /**
     * The namespaces of all the elements excluding namespaces
     * that representing elements.
     */
    val roots = elements.asSequence()
        .flatMap { it.namespace?.collect().orEmpty() }
        .minus(elementsMap.keys)
        .toSet()

    val failures = mutableListOf<GenException>()

    val createTypeNodes = mutableListOf<CreateTypeNode>()
    val injectTypeNodes = mutableListOf<InjectTypeNode>()
    val injectScopeNodes = mutableListOf<InjectScopeNode>()
}
