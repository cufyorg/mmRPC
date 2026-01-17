package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.AnnotationSpec
import org.cufy.mmrpc.MetadataUsage
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.common.code.createMetaLiteralCode
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.util.annotationSpec

@ContextScope
context(ctx: Context)
fun MetadataUsage.annotationSpec(): AnnotationSpec {
    return annotationSpec(definition.className()) {
        for (field in fields) {
            addMember(
                format = "%L = %L",
                field.definition.nameOfProperty(),
                createMetaLiteralCode(
                    field.definition.type,
                    field.value
                ),
            )
        }
    }
}
