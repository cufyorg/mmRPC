package org.cufy.mmrpc.gen.kotlin.util

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition

/**
 * For any element that has a member field holding its mmrpc reflection info,
 * this is the name of said member field.
 */
const val F_OBJECT_INFO = "__info__"

/**
 * For any element that has a static field holding its mmrpc reflection info,
 * this is the name of said static field.
 */
const val F_STATIC_INFO = "__INFO__"

/**
 * For any element that has a static field holding its runtime value,
 * this is the name of said static field.
 */
const val F_STATIC_VALUE = "VALUE"

/**
 * For any element that has a static field holding its name,
 * this is the name of said static field.
 */
const val F_STATIC_NAME = "NAME"

/**
 * For any element that has a static field holding its `path` value,
 * this is the name of said static field.
 */
const val F_STATIC_PATH = "PATH"

/**
 * For any element that has a static field holding its `topic` value,
 * this is the name of said static field.
 */
const val F_STATIC_TOPIC = "TOPIC"

/**
 * Assuming this const is a member of some enum,
 * this is the name of the generated entry by this const.
 */
val ConstDefinition.asEnumEntryName: String
    get() = name.furtherEscape().toScreamingSnakeCase()

/**
 * Assuming this element is a member of some union,
 * this is the name of the generated entry by this element.
 */
val ElementDefinition.asUnionEntryName: String
    get() = name.furtherEscape().toScreamingSnakeCase()

/**
 * Return the name of the property generated from this element (assuming it has one).
 */
val FieldDefinition.asPropertyName: String
    get() = name.furtherEscape().toCamelCase()

/**
 * Return the name of the generated class (assuming it has one) of this element.
 */
val ElementDefinition.asClassName: String
    get() = name.furtherEscape().toPascalCase()
