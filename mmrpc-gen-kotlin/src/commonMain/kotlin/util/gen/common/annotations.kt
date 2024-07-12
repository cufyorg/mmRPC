package org.cufy.mmrpc.gen.kotlin.util.gen.common

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.featureKotlinxSerialization
import org.cufy.mmrpc.gen.kotlin.util.poet.annotationSpec

@Marker3
fun GenGroup.createSerializableAnnotationSet(): Set<AnnotationSpec> {
    return buildSet {
        if (ctx.featureKotlinxSerialization) {
            val annotation = annotationSpec(Serializable::class)
            add(annotation)
        }
    }
}

@Marker3
fun GenGroup.createSerialNameAnnotationSet(serialName: String): Set<AnnotationSpec> {
    return createSerialNameAnnotationSet(CodeBlock.of("%S", serialName))
}

@Marker3
fun GenGroup.createSerialNameAnnotationSet(serialName: CodeBlock): Set<AnnotationSpec> {
    return buildSet {
        if (ctx.featureKotlinxSerialization) {
            val annotation = annotationSpec(SerialName::class) {
                addMember("value = %L", serialName)
            }

            add(annotation)
        }
    }
}
