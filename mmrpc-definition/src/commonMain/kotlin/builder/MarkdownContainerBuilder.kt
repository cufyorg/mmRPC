package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.intellij.lang.annotations.Language

////////////////////////////////////////

@Marker2
fun interface MarkdownContainerBuilder {
    fun addMarkdown(@Language("markdown") value: String)
}

////////////////////////////////////////

context(ctx: MarkdownContainerBuilder)
operator fun @receiver:Language("markdown") String.unaryPlus() {
    ctx.addMarkdown(this.trimIndent())
}

////////////////////////////////////////
