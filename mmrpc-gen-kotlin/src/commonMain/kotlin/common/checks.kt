package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenContext

/**
 * Return true, if the given [this] is supposed to have a generated class.
 */
@Marker3
context(ctx: GenContext)
fun ElementDefinition.hasGeneratedClass(): Boolean {
    if (this is ArrayDefinition) {
        if (!name[0].isUpperCase()) return false
        if (namespace !in ctx.roots) return false
        return true
    }
    if (this is MapDefinition) {
        if (!name[0].isUpperCase()) return false
        if (namespace !in ctx.roots) return false
        return true
    }
    if (this is OptionalDefinition) return false
    if (this is FieldDefinition) return false
    if (this is ConstDefinition) return false
    if (this is ScalarDefinition) {
        if (isNative() || isUserdefined())
            return false
    }
    if (this is MetadataDefinition) {
        if (isNative() || isUserdefined())
            return false
    }
    if (this is StructDefinition) {
        if (canonicalName == builtin.Unit.canonicalName)
            return false
    }
    val parent = resolveParent() ?: return true
    return parent.hasGeneratedClass()
}

/**
 * Return true, if the given type [this] can have `const` modifier
 * when a value with [this] as its type was generated.
 */
@Marker3
context(ctx: GenContext)
fun TypeDefinition.isCompileConst(): Boolean {
    return this is ScalarDefinition && isNative()
}

/**
 * Return true, if the given [this] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
context(ctx: GenContext)
fun ScalarDefinition.isNative(): Boolean {
    return canonicalName in ctx.nativeScalarClasses
}

/**
 * Return true, if the given [this] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
context(ctx: GenContext)
fun MetadataDefinition.isNative(): Boolean {
    return canonicalName in ctx.nativeMetadataClasses
}

/**
 * Return true, if the given [this] was declared
 * native (fully inlined) by the user.
 */
@Marker3
context(ctx: GenContext)
fun ConstDefinition.isNative(): Boolean {
    return canonicalName in ctx.nativeConstants
}

/**
 * Return true, if the given [this] was declared
 * defined in user code by the user.
 */
@Marker3
context(ctx: GenContext)
fun ScalarDefinition.isUserdefined(): Boolean {
    return canonicalName in ctx.userdefinedScalarClasses
}

/**
 * Return true, if the given [this] was declared
 * defined in user code by the user.
 */
@Marker3
context(ctx: GenContext)
fun MetadataDefinition.isUserdefined(): Boolean {
    return canonicalName in ctx.userdefinedMetadataClasses
}
