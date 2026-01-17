package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import net.pearx.kasechange.toPascalCase
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.StringLiteral
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug

////////////////////////////////////////

/**
 * Assuming [this] is a member of some enum,
 * this is the name of the generated entry by [this].
 */
@ContextScope
context(ctx: Context)
fun ConstDefinition.nameOfEnumEntry(): String {
    if (GenFeature.KEEP_ENUM_ENTRY_NAMES in ctx.features)
        return name

    return name.toPascalCase()
}

fun ConstDefinition.enumEntrySerialName(): String {
    return when (val literal = value) {
        is StringLiteral -> literal.value
        else -> "\"${literal.contentToString()}\""
    }
}

@ContextScope
context(ctx: Context)
fun EnumDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////
