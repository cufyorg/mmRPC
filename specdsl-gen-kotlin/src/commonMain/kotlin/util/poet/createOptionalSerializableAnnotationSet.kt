package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.AnnotationSpec
import kotlinx.serialization.Serializable
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.gen.kotlin.GenFeature
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createOptionalSerializableAnnotationSet(): Set<AnnotationSpec> {
    if (GenFeature.KOTLINX_SERIALIZATION !in ctx.features)
        return emptySet()

    return buildSet {
        if (GenFeature.KOTLINX_SERIALIZATION in ctx.features) {
            val annotationSpec = AnnotationSpec
                .builder(Serializable::class)
                .build()

            add(annotationSpec)
        }
    }
}
