package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.furtherEscape
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRequireGeneratedClass

private const val TAG = "generatedClassOf"

/**
 * Assuming the given [element] has a generated class,
 * return the kotlin-poet classname of said generated class.
 */
@Marker3
fun GenScope.generatedClassOf(element: ElementDefinition): ClassName {
    debugRequireGeneratedClass(TAG, element)

    return when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> implSubPackages(element)
    }
}

private fun GenScope.implSubPackages(element: ElementDefinition): ClassName {
    val ns = rootNSOf(element)
    val nsv = ns.canonicalName.value.furtherEscape()
    val pkg = when {
        ctx.packageName.isEmpty() -> nsv
        else -> "${ctx.packageName}.$nsv"
    }
    val names = element.asNamespace.collect()
        .drop(ns.segments.size)
        .map { ctx.elementsNS[it] }
        .filterNotNull() // ??? why would this happen? it is possible to have nulls yet unexpected
        .map { asClassName(it) }
        .toList()

    return ClassName(pkg, names)
}
