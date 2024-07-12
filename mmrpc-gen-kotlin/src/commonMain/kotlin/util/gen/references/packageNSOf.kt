package org.cufy.mmrpc.gen.kotlin.util.gen.references

import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.Namespace
import org.cufy.mmrpc.gen.kotlin.GenGroup

private const val MSG_TOPLEVEL_MISSING = "Unexpected Internal State: Namespace.Toplevel not in packagesNS"

@Marker3
fun GenGroup.rootNSOf(element: ElementDefinition): Namespace {
    var pkg = element.namespace

    while (pkg !in ctx.rootsNS)
        pkg = pkg.parentOrNull ?: error(MSG_TOPLEVEL_MISSING)

    return pkg
}
