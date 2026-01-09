package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias MetadataUsageBlock = context(MetadataUsageBuilder) () -> Unit

@Marker2
class MetadataUsageBuilder :
    FieldUsageContainerBuilder {
    val definition = Box<MetadataDefinition>()
    val fields = mutableListOf<FieldUsage>()

    override fun addFieldUsage(value: FieldUsage) {
        fields += value
    }

    fun build(): MetadataUsage {
        return MetadataUsage(
            definition = this::definition.getOrThrow(),
            fields = this.fields.toList(),
        )
    }
}

////////////////////////////////////////

operator fun MetadataDefinition.invoke(
    block: MetadataUsageBlock = {}
): MetadataUsage {
    return MetadataUsageBuilder()
        .also { it.definition *= this }
        .apply(block)
        .build()
}

////////////////////////////////////////
