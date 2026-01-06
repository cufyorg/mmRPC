package org.cufy.mmrpc.gen.kotlin.common

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature

/**
 * Assuming [this] is a member of some enum,
 * this is the name of the generated entry by [this].
 */
@Marker3
context(ctx: GenContext)
fun ConstDefinition.nameOfEnumEntry(): String {
    return name.toPascalCase()
}

/**
 * Assuming [this] is a member of some union,
 * this is the name of the generated entry by [this].
 */
@Marker3
context(ctx: GenContext)
fun ElementDefinition.nameOfUnionWrapperEntry(): String {
    if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
        return name

    return name.toPascalCase()
}

/**
 * Return the name of the property generated from [this]'s name (assuming it has one).
 */
@Marker3
context(ctx: GenContext)
fun FieldDefinition.nameOfNameProperty(): String {
    return name.toScreamingSnakeCase()
}

/**
 * Return the name of the property generated from [this] (assuming it has one).
 */
@Marker3
context(ctx: GenContext)
fun FieldDefinition.nameOfProperty(): String {
    if (GenFeature.KEEP_FIELD_PROPERTY_NAMES in ctx.features)
        return name

    return name.toCamelCase()
}

/**
 * Return the name of the property generated from [this] (assuming it has one).
 */
@Marker3
context(ctx: GenContext)
fun ConstDefinition.nameOfProperty(): String {
    return name.toScreamingSnakeCase()
}

/**
 * Return the name of the generated class (assuming it has one) of [this].
 */
@Marker3
context(ctx: GenContext)
fun ElementDefinition.nameOfClass(): String {
    ctx.classNames[canonicalName]?.let { return it }

    if (this is ProtocolDefinition) {
        return name.toPascalCase()
            .plus(ctx.protocolSuffix)
    }

    if (this is TypeDefinition) {
        if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
            return name
    }

    if (this is FaultDefinition) {
        if (GenFeature.KEEP_FAULT_CLASS_NAMES in ctx.features)
            return name

        return name.toScreamingSnakeCase()
    }

    return name.toPascalCase()
}
