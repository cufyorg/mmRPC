package org.cufy.mmrpc.gen.kotlin.util.gen.references

import com.squareup.kotlinpoet.ClassName
import net.pearx.kasechange.toPascalCase
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
        ctx.pkg.isEmpty() -> nsv
        else -> "${ctx.pkg}.$nsv"
    }
    val names = element.asNamespace.segments.asSequence()
        .drop(ns.segments.size)
        .map { it.furtherEscape().toPascalCase() }
        .toList()
    return ClassName(pkg, names)
}
