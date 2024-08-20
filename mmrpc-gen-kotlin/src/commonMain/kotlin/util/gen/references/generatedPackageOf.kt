package org.cufy.mmrpc.gen.kotlin.util.gen.references

import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.furtherEscape
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRequireGeneratedClass

private const val TAG = "generatePackageOf"

/**
 * Assuming the given [element] has a generated class,
 * return the package of said generated class.
 */
@Marker3
fun GenScope.generatedPackageOf(element: ElementDefinition): String {
    debugRequireGeneratedClass(TAG, element)

    return when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> implSubPackages(element)
    }
}

private fun GenScope.implSubPackages(element: ElementDefinition): String {
    val ns = rootNSOf(element)
    val nsv = ns.canonicalName.value.furtherEscape()
    return when {
        ctx.packageName.isEmpty() -> nsv
        else -> "${ctx.packageName}.$nsv"
    }
}
