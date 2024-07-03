package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import org.cufy.specdsl.Namespace
import org.cufy.specdsl.gen.kotlin.GenGroup

fun GenGroup.createBoxedNamespace(namespace: Namespace): CodeBlock {
    val segments = namespace.segments.joinToCode { CodeBlock.of("%S", it) }
    return CodeBlock.of("%T(%L)", Namespace::class, segments)
}
