package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec

fun generateFileSpecSet(
    ctx: GenContext,
    onEachFile: FileSpec.Builder.() -> Unit = {}
): List<FileSpec> {
    return when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> generateFileSpecSet__SUB_PACKAGES(ctx, onEachFile)
    }
}
