package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope

private const val TAG = "classes.kt"

/**
 * Returns the assignable type of some element.
 */
@Marker3
fun GenScope.classOf(element: TypeDefinition): TypeName {
    return when (element) {
        is OptionalDefinition,
        -> classOf(element.type).copy(nullable = true)

        is ArrayDefinition,
        -> LIST.parameterizedBy(classOf(element.type))

        is ScalarDefinition,
        -> when {
            isUserdefined(element) -> userdefinedClassOf(element)
            isNative(element) -> nativeClassOf(element)
            hasGeneratedClass(element) -> generatedClassOf(element.canonicalName)
            else -> ANY
        }

        is StructDefinition,
        -> when {
            element.canonicalName == builtin.Void.canonicalName -> Unit::class.asClassName()
            hasGeneratedClass(element) -> generatedClassOf(element.canonicalName)
            else -> ANY
        }

        is UnionDefinition,
        is EnumDefinition,
        is InterDefinition,
        is TupleDefinition,
        -> when {
            hasGeneratedClass(element) -> generatedClassOf(element.canonicalName)
            else -> ANY
        }
    }
}

@Marker3
fun GenScope.classOf(element: MetadataDefinition): ClassName {
    return when {
        isNative(element) -> nativeClassOf(element)
        isUserdefined(element) -> userdefinedClassOf(element)
        hasGeneratedClass(element) -> generatedClassOf(element.canonicalName)
        else -> fail(TAG, element) { "Cannot determine annotation class of element." }
    }
}

/**
 * Returns the assignable kotlin-annotation-compatible type of some element.
 */
@Marker3
fun GenScope.metaClassOf(element: TypeDefinition): TypeName {
    return when (element) {
        is ArrayDefinition,
        -> ARRAY.parameterizedBy(classOf(element.type))

        is ScalarDefinition,
        -> primitiveClassOf(element)

        is EnumDefinition,
        -> generatedClassOf(element.canonicalName)

        is OptionalDefinition,
        is UnionDefinition,
        is StructDefinition,
        is InterDefinition,
        is TupleDefinition,
        -> fail(TAG, element) { "metaTypeOf: element not supported" }
    }
}

/**
 * Return the name of the class that actually stores the values of the given [element].
 */
@Marker3
fun GenScope.primitiveClassOf(element: ScalarDefinition): ClassName {
    if (isNative(element)) return nativeClassOf(element)
    return ctx.scalarClasses[element.canonicalName]
        ?: element.type?.let { primitiveClassOf(it) }
        ?: ctx.defaultScalarClass
        ?: fail(TAG, element) { "element class is not set nor a default class" }
}

/**
 * Return the name of the native class that represents the given [element].
 * Assuming the [element] was declared by the user to be a native kotlin class.
 */
@Marker3
fun GenScope.nativeClassOf(element: MetadataDefinition): ClassName {
    debug { if (!isNative(element)) fail(TAG, element) { "element not native" } }
    return ctx.nativeMetadataClasses[element.canonicalName]
        ?: fail(TAG, element) { "element class is not set" }
}

/**
 * Return the name of the native class that represents the given [element].
 * Assuming the [element] was declared by the user to be a native kotlin class.
 */
@Marker3
fun GenScope.nativeClassOf(element: ScalarDefinition): ClassName {
    debug { if (!isNative(element)) fail(TAG, element) { "element not native" } }
    return ctx.nativeScalarClasses[element.canonicalName]
        ?: fail(TAG, element) { "no element to native class mapping" }
}

/**
 * Return the name of the class defined in user code that represents the given [element].
 * Assuming the [element] was declared by the user to be defined in user code.
 */
@Marker3
fun GenScope.userdefinedClassOf(element: MetadataDefinition): ClassName {
    debug { if (!isUserdefined(element)) fail(TAG, element) { "element not userdefined" } }
    return ctx.userdefinedMetadataClasses[element.canonicalName]
        ?: fail(TAG, element) { "element class is not set" }
}

/**
 * Return the name of the class defined in user code that represents the given [element].
 * Assuming the [element] was declared by the user to be defined in user code.
 */
@Marker3
fun GenScope.userdefinedClassOf(element: ScalarDefinition): ClassName {
    debug { if (!isUserdefined(element)) fail(TAG, element) { "element not userdefined" } }
    return ctx.userdefinedScalarClasses[element.canonicalName]
        ?: fail(TAG, element) { "no element to userdefined-class mapping was set" }
}
