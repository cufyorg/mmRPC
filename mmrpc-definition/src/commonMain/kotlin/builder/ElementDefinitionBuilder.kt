package org.cufy.mmrpc.builder

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.MetadataUsage

@Marker2
sealed class ElementDefinitionBuilder :
    MarkdownContainerBuilder,
    MetadataUsageContainerBuilder {
    lateinit var name: String
    var namespace: CanonicalName? = null
    var description = ""
    val metadata = mutableListOf<MetadataUsage>()

    override fun addMarkdown(value: String) {
        description += value
    }

    override fun addMetadataUsage(value: MetadataUsage) {
        metadata += value
    }

    protected fun buildCanonicalName() =
        CanonicalName(namespace, name)
}
