package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenPackaging

private const val TAG = "generated.kt"

@Marker3
context(ctx: GenContext)
fun declarationsClassOf(canonicalName: CanonicalName?): ClassName {
//    debug { if (namespace !in ctx.rootsNS) fail(TAG) { "generatedClassOf($namespace): namespace is not in rootsNS" } }

    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val nsv = canonicalName?.value.orEmpty()
            val pkg = when {
                ctx.packageName.isEmpty() -> nsv
                else -> "${ctx.packageName}.$nsv"
            }

            return ClassName(pkg, "DeclarationsKt")
        }
    }
}

/**
 * Assuming the given [canonicalName] has a generated class,
 * return the kotlin-poet classname of said generated class.
 */
@Marker3
context(ctx: GenContext)
fun generatedClassOf(canonicalName: CanonicalName): ClassName {
    debugRequireGeneratedClass(TAG, canonicalName)

    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val ns = rootNSOf(canonicalName)
            val nsv = ns?.value.orEmpty()
            val pkg = when {
                ctx.packageName.isEmpty() -> nsv
                else -> "${ctx.packageName}.$nsv"
            }
            val names = canonicalName.collect()
                .drop(ns?.segmentsCount() ?: 0)
                .map { ctx.elementsMap[it] }
                .filterNotNull() // ??? why would this happen? it is possible to have nulls yet unexpected
                .map { asClassName(it) }
                .toList()

            return ClassName(pkg, names)
        }
    }
}

/**
 * Assuming the given [canonicalName] has a generated class,
 * return the package of said generated class.
 */
@Marker3
context(ctx: GenContext)
fun generatedPackageOf(canonicalName: CanonicalName): String {
    debugRequireGeneratedClass(TAG, canonicalName)

    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val ns = rootNSOf(canonicalName)
            val nsv = ns?.value.orEmpty()
            return when {
                ctx.packageName.isEmpty() -> nsv
                else -> "${ctx.packageName}.$nsv"
            }
        }
    }
}

/**
 * Return the namespace of the top most element the given [canonicalName] is on.
 */
@Marker3
context(ctx: GenContext)
private fun rootNSOf(canonicalName: CanonicalName): CanonicalName? {
    var pkg = canonicalName.namespace

    while (pkg !in ctx.roots)
        pkg = pkg?.namespace ?: return null

    return pkg
}
