package org.cufy.mmrpc.gen.kotlin.common.model

import net.pearx.kasechange.toCamelCase
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.context.Context

fun FieldDefinition.propertySerialName(): String {
    return key ?: name
}

@ContextScope
context(ctx: Context)
fun FieldDefinition.nameOfProperty(): String {
    key?.let { return it }

    if (GenFeature.KEEP_FIELD_PROPERTY_NAMES in ctx.features)
        return name

    return name.toCamelCase()
}
