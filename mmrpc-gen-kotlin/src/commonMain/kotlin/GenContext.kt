package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Namespace
import org.cufy.mmrpc.SpecSheet
import org.cufy.mmrpc.builtin

class GenContext(
    val specSheet: SpecSheet,

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
     * All the elements.
     */
    val elements = buildSet {
        if (!featureNoBuiltin) {
            this += builtin.Any
            this += builtin.NULL
            this += builtin.String
            this += builtin.Boolean
            this += builtin.TRUE
            this += builtin.FALSE
            this += builtin.Int32
            this += builtin.UInt32
            this += builtin.Int64
            this += builtin.UInt64
            this += builtin.Float32
            this += builtin.Float64
            this += builtin.Deprecated
            this += builtin.Experimental
        }

        this.addAll(specSheet.elements)
    }

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
