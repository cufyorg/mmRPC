package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenContext

private const val TAG = "typeName.kt"

/**
 * Returns the assignable type of some element.
 */
@Marker3
context(ctx: GenContext)
fun TypeDefinition.typeName(): TypeName {
    return when (this) {
        is OptionalDefinition,
        -> type.typeName().copy(nullable = true)

        is ArrayDefinition,
        -> when {
            hasGeneratedClass() -> canonicalName.generatedClassName()
            else -> LIST.parameterizedBy(type.typeName())
        }

        is MapDefinition,
        -> when {
            hasGeneratedClass() -> canonicalName.generatedClassName()
            else -> MAP.parameterizedBy(STRING, type.typeName())
        }

        is ScalarDefinition,
        -> when {
            isUserdefined() -> userdefinedTypeName()
            isNative() -> nativeTypeName()
            hasGeneratedClass() -> canonicalName.generatedClassName()
            else -> ANY
        }

        is StructDefinition,
        -> when {
            canonicalName == builtin.Void.canonicalName -> Unit::class.asClassName()
            hasGeneratedClass() -> canonicalName.generatedClassName()
            else -> ANY
        }

        is UnionDefinition,
        is EnumDefinition,
        is InterDefinition,
        is TupleDefinition,
        is TraitDefinition,
        -> when {
            hasGeneratedClass() -> canonicalName.generatedClassName()
            else -> ANY
        }
    }
}

@Marker3
context(ctx: GenContext)
fun MetadataDefinition.typeName(): ClassName {
    return when {
        isNative() -> nativeTypeName()
        isUserdefined() -> userdefinedTypeName()
        hasGeneratedClass() -> canonicalName.generatedClassName()
        else -> fail(TAG, this) { "Cannot determine annotation class of element." }
    }
}

/**
 * Returns the assignable kotlin-annotation-compatible type of some element.
 */
@Marker3
context(ctx: GenContext)
fun TypeDefinition.metaTypeName(): TypeName {
    return when (this) {
        is ArrayDefinition,
        -> ARRAY.parameterizedBy(type.typeName())

        is ScalarDefinition,
        -> primitiveTypeName()

        is EnumDefinition,
        -> canonicalName.generatedClassName()

        is MapDefinition,
        is OptionalDefinition,
        is UnionDefinition,
        is StructDefinition,
        is InterDefinition,
        is TupleDefinition,
        is TraitDefinition,
        -> fail(TAG, this) { "metaTypeOf: element not supported" }
    }
}

/**
 * Return the name of the class that actually stores the values of the given [this].
 */
@Marker3
context(ctx: GenContext)
fun ScalarDefinition.primitiveTypeName(): ClassName {
    if (isNative()) return nativeTypeName()
    return ctx.scalarClasses[canonicalName]
        ?: type?.primitiveTypeName()
        ?: ctx.defaultScalarClass
        ?: fail(TAG, this) { "element class is not set nor a default class" }
}

/**
 * Return the name of the native class that represents the given [this].
 * Assuming the [this] was declared by the user to be a native kotlin class.
 */
@Marker3
context(ctx: GenContext)
fun MetadataDefinition.nativeTypeName(): ClassName {
    debug { if (!isNative()) fail(TAG, this) { "element not native" } }
    return ctx.nativeMetadataClasses[canonicalName]
        ?: fail(TAG, this) { "element class is not set" }
}

/**
 * Return the name of the native class that represents the given [this].
 * Assuming the [this] was declared by the user to be a native kotlin class.
 */
@Marker3
context(ctx: GenContext)
fun ScalarDefinition.nativeTypeName(): ClassName {
    debug { if (!isNative()) fail(TAG, this) { "element not native" } }
    return ctx.nativeScalarClasses[canonicalName]
        ?: fail(TAG, this) { "no element to native class mapping" }
}

/**
 * Return the name of the class defined in user code that represents the given [this].
 * Assuming the [this] was declared by the user to be defined in user code.
 */
@Marker3
context(ctx: GenContext)
fun MetadataDefinition.userdefinedTypeName(): ClassName {
    debug { if (!isUserdefined()) fail(TAG, this) { "element not userdefined" } }
    return ctx.userdefinedMetadataClasses[canonicalName]
        ?: fail(TAG, this) { "element class is not set" }
}

/**
 * Return the name of the class defined in user code that represents the given [this].
 * Assuming the [this] was declared by the user to be defined in user code.
 */
@Marker3
context(ctx: GenContext)
fun ScalarDefinition.userdefinedTypeName(): ClassName {
    debug { if (!isUserdefined()) fail(TAG, this) { "element not userdefined" } }
    return ctx.userdefinedScalarClasses[canonicalName]
        ?: fail(TAG, this) { "no element to userdefined-class mapping was set" }
}
