/*
 *	Copyright 2024 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
@file:Suppress("INAPPLICABLE_JVM_NAME")

package org.cufy.mmrpc

import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

////////////////////////////////////////

abstract class NamespaceObject {
    var namespace: Namespace
        protected set

    constructor() {
        this.namespace = Namespace(inferSegment())
    }

    constructor(vararg segments: String) {
        this.namespace = Namespace(*segments)
    }

    constructor(parent: NamespaceObject) {
        this.namespace = parent.namespace + inferSegment()
    }

    constructor(parent: NamespaceObject, vararg segments: String) {
        this.namespace = parent.namespace + segments.asList()
    }

    private fun inferSegment(): String {
        return this::class.simpleName.orEmpty()
    }
}

////////////////////////////////////////

fun interface UnnamedBlock<out T> {
    operator fun invoke(ns: Namespace, name: String?, isInline: Boolean): T
}

class Unnamed<out T>(private val block: UnnamedBlock<T>) {
    constructor(block: (Namespace) -> T) : this({ ns, _, _ -> block(ns) })
    constructor(value: T) : this({ _, _, _ -> value })

    fun get(namespace: Namespace, isInline: Boolean = true) =
        block(namespace, name = null, isInline = isInline)

    fun get(namespace: Namespace, name: String, isInline: Boolean = true) =
        block(namespace, name, isInline = isInline)

    fun get(obj: NamespaceObject, isInline: Boolean = true) =
        block(obj.namespace, name = null, isInline = isInline)

    fun get(obj: NamespaceObject, name: String, isInline: Boolean = true) =
        block(obj.namespace, name, isInline = isInline)

    private val values = mutableMapOf<Pair<Namespace, String?>, T>()

    operator fun getValue(namespace: Namespace, property: KProperty<*>): T {
        return values.getOrPut(namespace to property.name) {
            val splits = property.name.split("__")
            val ns = namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n, isInline = false)
        }
    }

    operator fun getValue(obj: NamespaceObject, property: KProperty<*>): T {
        return values.getOrPut(obj.namespace to property.name) {
            val splits = property.name.split("__")
            val ns = obj.namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n, isInline = false)
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Unnamed<T> {
        return Unnamed { namespace, name, isInline -> block(namespace, name, isInline) }
    }
}

////////////////////////////////////////

open class DomainProperty<T> {
    lateinit var value: Unnamed<T>

    operator fun timesAssign(value: T) {
        this.value = Unnamed(value)
    }

    operator fun timesAssign(value: Unnamed<T>) {
        this.value = value
    }
}

open class OptionalDomainProperty<T> {
    var value: Unnamed<T>? = null

    operator fun timesAssign(value: T) {
        this.value = Unnamed(value)
    }

    operator fun timesAssign(value: Unnamed<T>) {
        this.value = value
    }
}

open class NamespaceDomainProperty {
    var value: Namespace = Namespace.Toplevel

    operator fun timesAssign(value: Namespace) {
        this.value = value
    }

    operator fun timesAssign(value: NamespaceObject) {
        this.value = value.namespace
    }
}

////////////////////////////////////////

@Marker0
interface EndpointDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedEndpointDefinition")
    operator fun Unnamed<EndpointDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedEndpointDefinition")
    operator fun Iterable<Unnamed<EndpointDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusEndpointDefinition")
    operator fun EndpointDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableEndpointDefinition")
    operator fun Iterable<EndpointDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker0
interface FaultDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedFaultDefinition")
    operator fun Unnamed<FaultDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedFaultDefinition")
    operator fun Iterable<Unnamed<FaultDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusFaultDefinition")
    operator fun FaultDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableFaultDefinition")
    operator fun Iterable<FaultDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker0
interface TypeDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedTypeDefinition")
    operator fun Unnamed<TypeDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedTypeDefinition")
    operator fun Iterable<Unnamed<TypeDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusTypeDefinition")
    operator fun TypeDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableTypeDefinition")
    operator fun Iterable<TypeDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker0
interface RoutineDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedRoutineDefinition")
    operator fun Unnamed<RoutineDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedRoutineDefinition")
    operator fun Iterable<Unnamed<RoutineDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusRoutineDefinition")
    operator fun RoutineDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableRoutineDefinition")
    operator fun Iterable<RoutineDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(
        block: RoutineDefinitionBuilder.() -> Unit
    ) {
        +Unnamed { namespace, _, _ ->
            RoutineDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .apply(block)
                .build()
        }
    }
}

@Marker0
interface FieldDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedFieldDefinition")
    operator fun Unnamed<FieldDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedFieldDefinition")
    operator fun Iterable<Unnamed<FieldDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusFieldDefinition")
    operator fun FieldDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableFieldDefinition")
    operator fun Iterable<FieldDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(
        type: TypeDefinition,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _, _ ->
            FieldDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }

    operator fun String.invoke(
        type: Unnamed<TypeDefinition>,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _, _ ->
            FieldDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }
}

@Marker0
interface MetadataParameterDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedMetadataParameterDefinition")
    operator fun Unnamed<MetadataParameterDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedMetadataParameterDefinition")
    operator fun Iterable<Unnamed<MetadataParameterDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusMetadataParameterDefinition")
    operator fun MetadataParameterDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableMetadataParameterDefinition")
    operator fun Iterable<MetadataParameterDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(
        type: TypeDefinition,
        block: MetadataParameterDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _, _ ->
            MetadataParameterDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }

    operator fun String.invoke(
        type: Unnamed<TypeDefinition>,
        block: MetadataParameterDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _, _ ->
            MetadataParameterDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }
}

////////////////////////////////////////

@Marker0
abstract class ElementDefinitionBuilder {
    abstract var name: String
    open val namespace = NamespaceDomainProperty()
    open var isInline = true
    open var description = ""

    protected open val metadata = mutableListOf<MetadataDefinitionUsage>()

    open operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    @JvmName("unaryPlusMetadata")
    operator fun MetadataDefinitionUsage.unaryPlus() {
        metadata += this
    }

    @JvmName("unaryPlusIterableMetadata")
    operator fun Iterable<MetadataDefinitionUsage>.unaryPlus() {
        metadata += this
    }

    @JvmName("unaryPlusMetadataDefinition")
    operator fun MetadataDefinition.unaryPlus() {
        val classThis = this@ElementDefinitionBuilder
        classThis.metadata += MetadataDefinitionUsageBuilder()
            .also { it.definition = this }
            .build()
    }

    abstract fun build(): ElementDefinition
}

////////////////////////////////////////
