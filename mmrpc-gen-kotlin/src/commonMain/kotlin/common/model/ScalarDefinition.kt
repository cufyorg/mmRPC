package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.cufy.mmrpc.ScalarDefinition
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
fun ScalarDefinition.isNative(): Boolean {
    return canonicalName in ctx.nativeScalarClasses
}

/**
 * Return the name of the native class that represents the given [this].
 * Assuming the [this] was declared by the user to be a native kotlin class.
 */
@ContextScope
context(ctx: Context)
fun ScalarDefinition.nativeClassName(): ClassName {
    debug { check(isNative()) }
    return ctx.nativeScalarClasses[canonicalName]!!
}

////////////////////////////////////////

/**
 * Return true, if the given [this] was declared
 * defined in user code by the user.
 */
@ContextScope
context(ctx: Context)
fun ScalarDefinition.isUserdefined(): Boolean {
    return canonicalName in ctx.userdefinedScalarClasses
}

/**
 * Return the name of the class defined in user code that represents the given [this].
 * Assuming the [this] was declared by the user to be defined in user code.
 */
@ContextScope
context(ctx: Context)
fun ScalarDefinition.userdefinedClassName(): ClassName {
    debug { check(isUserdefined()) }
    return ctx.userdefinedScalarClasses[canonicalName]!!
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ScalarDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun ScalarDefinition.className(): ClassName {
    return when {
        isNative() -> nativeClassName()
        isUserdefined() -> userdefinedClassName()
        hasGeneratedClass() -> generatedClassName()
        else -> fail(this, "Couldn't determine runtime class of scalar definition")
    }
}

/**
 * Return the name of the class that actually stores the values of the given [this].
 */
@ContextScope
context(ctx: Context)
fun ScalarDefinition.primitiveTypeName(): TypeName {
    if (isNative()) return nativeClassName()
    return ctx.scalarClasses[canonicalName]
        ?: type?.primitiveTypeName()
        ?: ctx.defaultScalarClass
        ?: fail(this, "element class is not set nor a default class")
}

////////////////////////////////////////
