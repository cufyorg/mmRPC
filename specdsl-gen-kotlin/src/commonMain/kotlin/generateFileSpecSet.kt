package org.cufy.specdsl.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.specdsl.gen.kotlin.util.asClassName

fun generateFileSpecSet(ctx: GenContext): List<FileSpec> {
    return buildList {
        for (ns in ctx.namespaceSet) {
            val objectBlocks = ctx.objectBlocks[ns].orEmpty()
            val objectOptionalBlocks = ctx.objectOptionalBlocks[ns].orEmpty()
            val fileBlocks = ctx.fileBlocks[ns].orEmpty()
            val fileOptionalBlocks = ctx.fileOptionalBlocks[ns].orEmpty()

            if (objectBlocks.isEmpty() && fileBlocks.isEmpty())
                continue

            val nsClass = ClassName(ctx.pkg, ns.asClassName)

            val typeSpec = TypeSpec.objectBuilder(nsClass)
                .apply { objectBlocks.forEach { it() } }
                .apply { objectOptionalBlocks.forEach { it() } }
                .addKdoc("### namespace ${ns.canonicalName.value}")
                .build()

            val fileSpec = FileSpec.builder(ctx.pkg, ns.canonicalName.value)
                .addType(typeSpec)
                .apply { fileBlocks.forEach { it() } }
                .apply { fileOptionalBlocks.forEach { it() } }
                .build()

            add(fileSpec)
        }

        val toplevelBlocks = ctx.toplevelBlocks

        if (toplevelBlocks.isNotEmpty()) {
            val toplevelSpec = FileSpec.builder(ctx.pkg, "toplevel")
                .apply { toplevelBlocks.forEach { it() } }
                .build()

            add(toplevelSpec)
        }
    }
}
