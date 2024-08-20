package org.cufy.mmrpc.gen.kotlin.util.gen.references

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.furtherEscape

/**
 * Assuming [element] is a member of some enum,
 * this is the name of the generated entry by [element].
 */
@Marker3
fun GenGroup.asEnumEntryName(element: ConstDefinition): String {
    return element.name.furtherEscape().toScreamingSnakeCase()
}

/**
 * Assuming [element] is a member of some union,
 * this is the name of the generated entry by [element].
 */
@Marker3
fun GenGroup.asUnionEntryName(element: ElementDefinition): String {
    return element.name.furtherEscape().toScreamingSnakeCase()
}

/**
 * Return the name of the property generated from [element] (assuming it has one).
 */
@Marker3
fun GenGroup.asPropertyName(element: FieldDefinition): String {
    return element.name.furtherEscape().toCamelCase()
}

/**
 * Return the name of the generated class (assuming it has one) of [element].
 */
@Marker3
fun GenGroup.asClassName(element: ElementDefinition): String {
    return element.name.furtherEscape().toPascalCase()
}
