package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.AnnotationSpec
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinitionUsage
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker3
fun GenGroup.createAnnotationSet(metadata: List<MetadataDefinitionUsage>): List<AnnotationSpec> {
    return buildList {
        for (it in metadata) {
            if (it.definition.isAnonymous) continue

            val annotationSpec = AnnotationSpec.builder(classOf(it.definition))
                .apply {
                    for (parameter in it.parameters) {
                        val name = parameter.definition.name
                        val literal = createLiteralInlined(
                            parameter.definition.parameterType,
                            parameter.value
                        )
                        addMember("%L = %L", name, literal)
                    }
                }
                .build()

            add(annotationSpec)
        }
    }
}
