package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.AnnotationSpec
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.MetadataDefinitionUsage
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
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
