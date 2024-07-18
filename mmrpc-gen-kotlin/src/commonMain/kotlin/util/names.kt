package org.cufy.mmrpc.gen.kotlin.util

import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toScreamingSnakeCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition

const val F_OBJECT_INFO = "__info__"
const val F_STATIC_INFO = "__INFO__"

const val F_STATIC_VALUE = "VALUE"

const val F_STATIC_NAME = "NAME"
const val F_STATIC_PATH = "PATH"
const val F_STATIC_TOPIC = "TOPIC"

val ConstDefinition.asEnumEntryName: String
    get() = name.furtherEscape().toScreamingSnakeCase()

val ElementDefinition.asUnionEntryName: String
    get() = name.furtherEscape().toScreamingSnakeCase()

val FieldDefinition.asPropertyName: String
    get() = name.furtherEscape().toCamelCase()

val ElementDefinition.asClassName: String
    get() = name.furtherEscape().toPascalCase()
