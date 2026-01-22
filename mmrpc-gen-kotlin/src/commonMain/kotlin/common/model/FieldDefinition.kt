package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ParameterSpec
import net.pearx.kasechange.toCamelCase
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.code.createLiteralCode
import org.cufy.mmrpc.gen.kotlin.common.code.createMetaLiteralCode
import org.cufy.mmrpc.gen.kotlin.common.metaTypeName
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec

fun FieldDefinition.propertySerialName(): String {
    return key ?: name
}

@ContextScope
context(ctx: Context)
fun FieldDefinition.nameOfProperty(): String {
    key?.let { key ->
        if (GenFeature.KEEP_FIELD_PROPERTY_NAMES in ctx.features)
            return key

        return key.toCamelCase()
    }

    if (GenFeature.KEEP_FIELD_PROPERTY_NAMES in ctx.features)
        return name

    return name.toCamelCase()
}

@ContextScope
context(ctx: Context)
fun FieldDefinition.parameterSpec(): ParameterSpec {
    return parameterSpec(nameOfProperty(), type.typeName()) {
        default?.let { defaultValue(createLiteralCode(type, it)) }
    }
}

@ContextScope
context(ctx: Context)
fun FieldDefinition.metaParameterSpec(): ParameterSpec {
    return parameterSpec(nameOfProperty(), type.metaTypeName()) {
        default?.let { defaultValue(createMetaLiteralCode(type, it)) }
    }
}
