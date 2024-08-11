package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import org.cufy.mmrpc.Namespace

/**
 * Return code that, when executed, returns the given [namespace].
 */
fun createBoxedNamespace(namespace: Namespace): CodeBlock {
    val segments = namespace.segments.joinToCode { CodeBlock.of("%S", it) }
    return CodeBlock.of("%T(%L)", Namespace::class, segments)
}
