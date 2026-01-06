package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinitionUsage
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.util.annotationSpec

@Marker3
context(ctx: GenContext)
fun createSerializableAnnotationSet(): Set<AnnotationSpec> {
    return buildSet {
        if (GenFeature.KOTLINX_SERIALIZATION in ctx.features) {
            add(annotationSpec(Serializable::class))
        }
    }
}

@Marker3
context(ctx: GenContext)
fun createSerialNameAnnotationSet(serialName: String): Set<AnnotationSpec> {
    return createSerialNameAnnotationSet(CodeBlock.of("%S", serialName))
}

@Marker3
context(ctx: GenContext)
fun createSerialNameAnnotationSet(serialName: CodeBlock): Set<AnnotationSpec> {
    return buildSet {
        if (GenFeature.KOTLINX_SERIALIZATION in ctx.features) {
            add(annotationSpec(SerialName::class) {
                addMember("value = %L", serialName)
            })
        }
    }
}

@Marker3
context(ctx: GenContext)
fun createAnnotationSet(metadata: List<MetadataDefinitionUsage>): List<AnnotationSpec> {
    return buildList {
        for (it in metadata) {
            add(annotationSpec(it.definition.typeName()) {
                for (usage in it.fields) {
                    addMember(
                        format = "%L = %L",
                        usage.definition.nameOfProperty(),
                        createMetaLiteralCode(
                            usage.definition.type,
                            usage.value
                        ),
                    )
                }
            })
        }
    }
}
