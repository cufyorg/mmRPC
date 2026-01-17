package org.cufy.mmrpc.gen.kotlin.util

import com.squareup.kotlinpoet.AnnotationSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun createSerializable(): AnnotationSpec {
    return annotationSpec(Serializable::class)
}

fun createSerialName(serialName: String): AnnotationSpec {
    return annotationSpec(SerialName::class) {
        addMember("value = %S", serialName)
    }
}
