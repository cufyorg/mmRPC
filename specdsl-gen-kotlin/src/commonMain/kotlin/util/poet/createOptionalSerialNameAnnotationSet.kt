package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import kotlinx.serialization.SerialName
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.gen.kotlin.GenFeature
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createOptionalSerialNameAnnotationSet(name: String): Set<AnnotationSpec> {
    return createOptionalSerialNameAnnotationSet(CodeBlock.of("%S", name))
}

@Marker0
fun GenGroup.createOptionalSerialNameAnnotationSet(name: CodeBlock): Set<AnnotationSpec> {
    return buildSet {
        if (GenFeature.KOTLINX_SERIALIZATION in ctx.features) {
            val annotationSpec = AnnotationSpec
                .builder(SerialName::class)
                .addMember("value = %L", name)
                .build()

            add(annotationSpec)
        }
    }
}
