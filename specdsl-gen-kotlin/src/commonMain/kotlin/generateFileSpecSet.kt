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

            if (objectBlocks.isEmpty() && fileBlocks.isEmpty()) {
                // Ignore the namespace if it and all of its children are not used
                val cond0 = ctx.objectBlocks.none { it.key in ns && it.value.isNotEmpty() }
                val cond1 = ctx.fileBlocks.none { it.key in ns && it.value.isNotEmpty() }

                if (cond0 && cond1) continue
            }

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
    }
}
