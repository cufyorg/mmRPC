package org.cufy.mmrpc.gen.kotlin.util

import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private val TYPE_SPEC_BUILDER_KIND_PROPERTY by lazy {
    val p = TypeSpec.Builder::class.memberProperties
        .firstOrNull { it.name == "kind" }
        ?: error("Couldn't get property: com.squareup.kotlinpoet.TypeSpec.Builder.kind")
    p.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    p as KProperty1<TypeSpec.Builder, TypeSpec.Kind>
}

fun TypeSpec.Builder.fetchKind(): TypeSpec.Kind {
    return TYPE_SPEC_BUILDER_KIND_PROPERTY.get(this)
}
