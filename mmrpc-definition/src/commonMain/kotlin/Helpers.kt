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

import org.intellij.lang.annotations.Language
import kotlin.jvm.JvmName

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

open class OptionalLiteralDomainProperty {
    var value: Literal? = null

    operator fun timesAssign(value: Literal) {
        this.value = value
    }

    operator fun timesAssign(value: ConstDefinition) {
        this.value = value.value
    }
}

////////////////////////////////////////

@Marker3
interface StructDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedStructDefinition")
    operator fun Unnamed<StructDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedStructDefinition")
    operator fun Iterable<Unnamed<StructDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusStructDefinition")
    operator fun StructDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableStructDefinition")
    operator fun Iterable<StructDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

////////////////////////////////////////

    operator fun String.invoke(
        block: StructDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _ ->
            StructDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace = namespace }
                .apply(block)
                .build()
        }
    }

////////////////////////////////////////

    operator fun String.invoke(
        vararg fields: FieldDefinition,
        block: StructDefinitionBuilder.() -> Unit = {},
    ) = this { +fields.asList(); block() }

    operator fun String.invoke(
        vararg fields: Unnamed<FieldDefinition>,
        block: StructDefinitionBuilder.() -> Unit = {},
    ) = this { +fields.asList(); block() }
}

@Marker3
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

////////////////////////////////////////

    private operator fun String.invoke(
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _ ->
            FieldDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace = namespace }
                .apply(block)
                .build()
        }
    }

////////////////////////////////////////

    operator fun String.invoke(
        type: TypeDefinition,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) = this { this.type *= type; block() }

    operator fun String.invoke(
        type: Unnamed<TypeDefinition>,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) = this { this.type *= type; block() }
}

////////////////////////////////////////

@Marker3
abstract class ElementDefinitionBuilder {
    lateinit var name: String
    open var namespace: CanonicalName? = null

    @Language("markdown")
    open var description = ""

    protected open val metadata = mutableListOf<MetadataDefinitionUsage>()

    open operator fun @receiver:Language("markdown") String.unaryPlus() {
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
