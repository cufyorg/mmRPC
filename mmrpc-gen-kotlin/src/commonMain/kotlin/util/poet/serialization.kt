package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.cufy.mmrpc.Marker0
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenGroup

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

@Marker0
fun GenGroup.createOptionalTransientAnnotationSet(): Set<AnnotationSpec> {
    if (GenFeature.KOTLINX_SERIALIZATION !in ctx.features)
        return emptySet()

    return buildSet {
        if (GenFeature.KOTLINX_SERIALIZATION in ctx.features) {
            val annotationSpec = AnnotationSpec
                .builder(Transient::class)
                .build()

            add(annotationSpec)
        }
    }
}

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
