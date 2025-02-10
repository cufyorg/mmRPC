package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope

/**
 * Return true, if the given [element] is supposed to have a generated class.
 */
@Marker3
fun GenScope.hasGeneratedClass(element: ElementDefinition): Boolean {
    if (element is ArrayDefinition) {
        if (!element.name[0].isUpperCase()) return false
        if (element.namespace !in ctx.roots) return false
        return true
    }
    if (element is OptionalDefinition) return false
    if (element is FieldDefinition) return false
    if (element is ConstDefinition) return false
    if (element is ScalarDefinition) {
        if (isNative(element) || isUserdefined(element))
            return false
    }
    if (element is MetadataDefinition) {
        if (isNative(element) || isUserdefined(element))
            return false
    }
    if (element is StructDefinition) {
        if (element.canonicalName == builtin.Void.canonicalName)
            return false
    }
    val parent = ctx.elementsMap[element.namespace] ?: return true
    return hasGeneratedClass(parent)
}

/**
 * Return true, if the given type [element] can have `const` modifier
 * when a value with [it][element] as its type was generated.
 */
@Marker3
fun GenScope.isCompileConst(element: TypeDefinition): Boolean {
    return element is ScalarDefinition && isNative(element)
}

/**
 * Return true, if the given [element] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
fun GenScope.isNative(element: ScalarDefinition): Boolean {
    return element.canonicalName in ctx.nativeScalarClasses
}

/**
 * Return true, if the given [element] was declared
 * native (mapped to a native kotlin class) by the user.
 */
@Marker3
fun GenScope.isNative(element: MetadataDefinition): Boolean {
    return element.canonicalName in ctx.nativeMetadataClasses
}

/**
 * Return true, if the given [element] was declared
 * native (fully inlined) by the user.
 */
@Marker3
fun GenScope.isNative(element: ConstDefinition): Boolean {
    return element.canonicalName in ctx.nativeConstants
}

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenScope.isUserdefined(element: ScalarDefinition): Boolean {
    return element.canonicalName in ctx.userdefinedScalarClasses
}

/**
 * Return true, if the given [element] was declared
 * defined in user code by the user.
 */
@Marker3
fun GenScope.isUserdefined(element: MetadataDefinition): Boolean {
    return element.canonicalName in ctx.userdefinedMetadataClasses
}
