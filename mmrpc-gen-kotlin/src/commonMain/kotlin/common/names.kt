package org.cufy.mmrpc.gen.kotlin.common

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenScope

/**
 * Assuming [element] is a member of some enum,
 * this is the name of the generated entry by [element].
 */
@Marker3
fun GenScope.asEnumEntryName(element: ConstDefinition): String {
    return element.name.toPascalCase()
}

/**
 * Assuming [element] is a member of some union,
 * this is the name of the generated entry by [element].
 */
@Marker3
fun GenScope.asUnionWrapperEntryName(element: ElementDefinition): String {
    if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
        return element.name

    return element.name.toPascalCase()
}

/**
 * Return the name of the property generated from [element]'s name (assuming it has one).
 */
@Marker3
fun GenScope.asNamePropertyName(element: FieldDefinition): String {
    return element.name.toScreamingSnakeCase()
}

/**
 * Return the name of the property generated from [element] (assuming it has one).
 */
@Marker3
fun GenScope.asPropertyName(element: FieldDefinition): String {
    if (GenFeature.KEEP_FIELD_PROPERTY_NAMES in ctx.features)
        return element.name

    return element.name.toCamelCase()
}

/**
 * Return the name of the property generated from [element] (assuming it has one).
 */
@Marker3
fun GenScope.asPropertyName(element: ConstDefinition): String {
    return element.name.toScreamingSnakeCase()
}

/**
 * Return the name of the generated class (assuming it has one) of [element].
 */
@Marker3
fun GenScope.asClassName(element: ElementDefinition): String {
    ctx.classNames[element.canonicalName]?.let { return it }

    if (element is ProtocolDefinition) {
        return element.name.toPascalCase()
            .plus(ctx.protocolSuffix)
    }

    if (element is TypeDefinition) {
        if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
            return element.name
    }

    if (element is FaultDefinition) {
        if (GenFeature.KEEP_FAULT_CLASS_NAMES in ctx.features)
            return element.name

        return element.name.toScreamingSnakeCase()
    }

    return element.name.toPascalCase()
}
