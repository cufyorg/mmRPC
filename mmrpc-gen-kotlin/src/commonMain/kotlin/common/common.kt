package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.pearx.kasechange.toPascalCase
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.context.fail

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun CanonicalName?.toPackageName(): String {
    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val ns = this?.value
            val pkg = ctx.packageName
            return when {
                ns == null -> when {
                    pkg == null -> ""
                    else -> pkg
                }

                else -> when {
                    pkg == null -> ns
                    else -> "$pkg.$ns"
                }
            }
        }
    }
}

@ContextScope
context(ctx: Context)
fun CanonicalName.assumedPackageName(): String {
    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val ns = resolvePackageNS()?.value
            val pkg = ctx.packageName
            return when {
                ns == null -> when {
                    pkg == null -> ""
                    else -> pkg
                }

                else -> when {
                    pkg == null -> ns
                    else -> "$pkg.$ns"
                }
            }
        }
    }
}

@ContextScope
context(ctx: Context)
fun CanonicalName.assumedSimpleNames(): List<String> {
    when (ctx.packaging) {
        GenPackaging.SUB_PACKAGES -> {
            val skip = resolvePackageNS()?.segmentsCount() ?: 0
            return collect()
                .drop(skip)
                .map { it.resolveElement()!! }
                .map { it.nameOfClass() }
                .toList()
        }
    }
}

@ContextScope
context(ctx: Context)
fun CanonicalName.resolvePackageNS(): CanonicalName? {
    // Return the namespace of the top most element this element is on.

    var pkg = namespace ?: return null

    while (pkg !in ctx.roots)
        pkg = pkg.namespace ?: return null

    return pkg
}

@ContextScope
context(ctx: Context)
fun CanonicalName.resolveElement(): ElementDefinition? =
    ctx.elementsMap[this]

////////////////////////////////////////

/**
 * Return the name of the generated class (assuming it has one) of [this].
 */
@ContextScope
context(ctx: Context)
fun ElementDefinition.nameOfClass(): String {
    ctx.classNames[canonicalName]?.let { return it }

    if (this is ProtocolDefinition) {
        if (GenFeature.KEEP_PROTOCOL_CLASS_NAMES in ctx.features)
            return name
    }

    if (this is RoutineDefinition) {
        if (GenFeature.KEEP_ROUTINE_CLASS_NAMES in ctx.features)
            return name
    }

    if (this is TypeDefinition) {
        if (GenFeature.KEEP_TYPE_CLASS_NAMES in ctx.features)
            return name
    }

    if (this is FaultDefinition) {
        if (GenFeature.KEEP_FAULT_CLASS_NAMES in ctx.features)
            return name
    }

    return name.toPascalCase()
}

@ContextScope
context(ctx: Context)
fun ElementDefinition.hasGeneratedClass(): Boolean {
    val parent = resolveParent()

    if (parent != null && !parent.hasGeneratedClass())
        return false

    when (this) {
        is ConstDefinition,
        is FieldDefinition,
        is OptionalDefinition,
        is ArrayDefinition,
        is MapDefinition,
        -> return false

        is FaultDefinition,
        is EnumDefinition,
        is RoutineDefinition,
        is ProtocolDefinition,
        is TraitDefinition,
        is TupleDefinition,
        is UnionDefinition,
        -> return true

        is StructDefinition,
        -> return canonicalName != builtin.Unit.canonicalName

        is MetadataDefinition
        -> return !isNative() && !isUserdefined()

        is ScalarDefinition
        -> return !isNative() && !isUserdefined()
    }
}

@ContextScope
context(ctx: Context)
fun ElementDefinition.isGeneratingClass(): Boolean {
    if (this is ConstDefinition) return false
    if (this is FieldDefinition) return false
    if (this is OptionalDefinition) return false
    if (this is ArrayDefinition) return false
    if (this is MapDefinition) return false

    if (this is StructDefinition) {
        if (canonicalName == builtin.Unit.canonicalName)
            return false
    }
    if (this is MetadataDefinition) {
        if (isNative() || isUserdefined())
            return false
    }
    if (this is ScalarDefinition) {
        if (isNative() || isUserdefined())
            return false
    }

    val parent = resolveParent()

    if (parent != null) return parent.isGeneratingClass()
    if (this is RoutineDefinition) return false

    if (this is ProtocolDefinition) {
        return GenFeature.GENERATE_PROTOCOLS in ctx.features
    }

    return GenFeature.GENERATE_TYPES in ctx.features
}

@ContextScope
context(ctx: Context)
fun ElementDefinition.resolveParent(): ElementDefinition? =
    namespace?.resolveElement()

/**
 * Return a human-readable name of the given [this].
 */
fun ElementDefinition.humanSignature(): String {
    val discriminator = when (this) {
        is ArrayDefinition -> "array"
        is MapDefinition -> "map"
        is EnumDefinition -> "enum"
        is ConstDefinition -> "const"
        is FaultDefinition -> "fault"
        is FieldDefinition -> "field"
        is MetadataDefinition -> "metadata"
        is OptionalDefinition -> "optional"
        is ProtocolDefinition -> "protocol"
        is RoutineDefinition -> "routine"
        is ScalarDefinition -> "scalar"
        is TraitDefinition -> "trait"
        is StructDefinition -> "struct"
        is TupleDefinition -> "tuple"
        is UnionDefinition -> "union"
    }

    return "$discriminator ${canonicalName.value}"
}

////////////////////////////////////////

/**
 * Returns the assignable type of some element.
 */
@ContextScope
context(ctx: Context)
fun TypeDefinition.typeName(): TypeName {
    return when (this) {
        is OptionalDefinition ->
            type.typeName().copy(nullable = true)

        is ArrayDefinition -> when {
            hasGeneratedTypealias() -> generatedTypealias()
            else -> LIST.parameterizedBy(type.typeName())
        }

        is MapDefinition -> when {
            hasGeneratedTypealias() -> generatedTypealias()
            else -> MAP.parameterizedBy(STRING, type.typeName())
        }

        is ScalarDefinition -> className()

        is StructDefinition -> when {
            canonicalName == builtin.Unit.canonicalName -> Unit::class.asClassName()
            hasGeneratedClass() -> generatedClassName()
            else -> ANY
        }

        is UnionDefinition -> generatedClassName()
        is EnumDefinition -> generatedClassName()
        is TupleDefinition -> generatedClassName()
        is TraitDefinition -> generatedClassName()
    }
}

/**
 * Returns the assignable kotlin-annotation-compatible type of some element.
 */
@ContextScope
context(ctx: Context)
fun TypeDefinition.metaTypeName(): TypeName {
    return when (this) {
        is ArrayDefinition,
        -> ARRAY.parameterizedBy(type.metaTypeName())

        is ScalarDefinition,
        -> primitiveTypeName()

        is EnumDefinition,
        -> generatedClassName()

        is MapDefinition,
        is OptionalDefinition,
        is UnionDefinition,
        is StructDefinition,
        is TupleDefinition,
        is TraitDefinition,
        -> fail(this, "metaTypeOf: element not supported")
    }
}

@ContextScope
fun TypeDefinition.typeSerialName(): String {
    return when (this) {
        is EnumDefinition,
        is UnionDefinition,
        is ScalarDefinition,
        is StructDefinition,
        is TupleDefinition,
        is TraitDefinition,
        -> canonicalName.value

        is ArrayDefinition,
        is MapDefinition,
        is OptionalDefinition,
        -> fail(this, "Cannot produce serial name for element")
    }
}

/**
 * Return true, if the given type [this] can have `const` modifier
 * when a value with [this] as its type was generated.
 */
@ContextScope
context(ctx: Context)
fun TypeDefinition.isCompileConst(): Boolean {
    return this is ScalarDefinition && isNative()
}

////////////////////////////////////////
