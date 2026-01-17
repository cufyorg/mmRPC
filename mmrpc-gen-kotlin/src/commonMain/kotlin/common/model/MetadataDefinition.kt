package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.context.fail

////////////////////////////////////////

/**
 * Return true, if the given [this] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@ContextScope
context(ctx: Context)
fun MetadataDefinition.isNative(): Boolean {
    return canonicalName in ctx.nativeMetadataClasses
}

/**
 * Return the name of the native class that represents the given [this].
 * Assuming the [this] was declared by the user to be a native kotlin class.
 */
@ContextScope
context(ctx: Context)
fun MetadataDefinition.nativeClassName(): ClassName {
    debug { check(isNative()) }
    return ctx.nativeMetadataClasses[canonicalName]!!
}

////////////////////////////////////////

/**
 * Return true, if the given [this] was declared
 * defined in user code by the user.
 */
@ContextScope
context(ctx: Context)
fun MetadataDefinition.isUserdefined(): Boolean {
    return canonicalName in ctx.userdefinedMetadataClasses
}

/**
 * Return the name of the class defined in user code that represents the given [this].
 * Assuming the [this] was declared by the user to be defined in user code.
 */
@ContextScope
context(ctx: Context)
fun MetadataDefinition.userdefinedClassName(): ClassName {
    debug { check(isUserdefined()) }
    return ctx.userdefinedMetadataClasses[canonicalName]!!
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun MetadataDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun MetadataDefinition.className(): ClassName {
    return when {
        isNative() -> nativeClassName()
        isUserdefined() -> userdefinedClassName()
        hasGeneratedClass() -> generatedClassName()
        else -> fail(this, "Couldn't determine runtime class of metadata definition")
    }
}

////////////////////////////////////////
