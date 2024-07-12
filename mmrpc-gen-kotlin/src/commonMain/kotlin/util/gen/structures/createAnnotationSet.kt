package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.AnnotationSpec
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinitionUsage
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asPropertyName
import org.cufy.mmrpc.gen.kotlin.util.gen.references.annotationClassOf

@Marker3
fun GenGroup.createAnnotationSet(metadata: List<MetadataDefinitionUsage>): List<AnnotationSpec> {
    return buildList {
        for (it in metadata) {
            if (it.definition.isAnonymous) continue

            val annotationSpec = AnnotationSpec.builder(annotationClassOf(it.definition))
                .apply {
                    for (usage in it.fields) {
                        val name = usage.definition.asPropertyName
                        val literal = createLiteralInlined(
                            usage.definition.fieldType,
                            usage.value
                        )
                        addMember("%L = %L", name, literal)
                    }
                }
                .build()

            add(annotationSpec)
        }
    }
}
