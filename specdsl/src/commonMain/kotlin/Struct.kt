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
package org.cufy.specdsl

import kotlin.reflect.KProperty

////////////////////////////////////////

sealed interface Struct : Type {
    val fields: List<Field>

    override fun collectChildren() =
        sequence { yieldAll(fields.asSequence().flatMap { it.collect() }) }
}

abstract class StructBuilder {
    abstract operator fun Field.unaryPlus()

    operator fun String.invoke(type: Type, block: AnonymousFieldBuilder.() -> Unit = {}) {
        +AnonymousFieldBuilder()
            .also { it.name = this }
            .also { it.type = type }
            .also(block)
            .build()
    }

    abstract fun build(): Struct
}

////////////////////////////////////////

data class StructDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val isInline: Boolean,
    override val description: String,
    override val fields: List<FieldDefinition>,
) : Struct, TypeDefinition {
    override fun collectChildren() =
        sequence { yieldAll(fields.asSequence().flatMap { it.collect() }) }
}

////////////////////////////////////////

open class StructDefinitionBuilder : StructBuilder() {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    // language=markdown
    open var description = ""

    open operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    protected open var fields = mutableListOf<FieldDefinition>()
    protected open var anonymousFields = mutableListOf<AnonymousField>()

    override operator fun Field.unaryPlus() {
        when (this) {
            is FieldDefinition -> fields += this
            is AnonymousField -> anonymousFields += this
        }
    }

    override fun build(): StructDefinition {
        val asNamespace = this.namespace + this.name
        return StructDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = false,
            description = this.description,
            fields = this.fields +
                    this.anonymousFields.map {
                        it.createDefinition(asNamespace)
                    }
        )
    }
}

@Marker1
val struct = UnnamedProvider { namespace, name ->
    StructDefinitionBuilder()
        .also { it.name = name }
        .also { it.namespace = namespace }
        .build()
}

@Marker1
fun struct(block: StructDefinitionBuilder.() -> Unit = {}): Unnamed<Struct> {
    return Unnamed { namespace, name ->
        StructDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

data class AnonymousStruct(
    override val fields: List<Field>,
) : Struct, AnonymousType {
    override fun createDefinition(namespace: Namespace): StructDefinition {
        val name = "(anonymous{})"
        val asNamespace = namespace + name
        return StructDefinition(
            name = name,
            namespace = namespace,
            isInline = true,
            description = "",
            fields = this.fields.map {
                when (it) {
                    is FieldDefinition -> it
                    is AnonymousField -> it.createDefinition(asNamespace)
                }
            }
        )
    }

    operator fun provideDelegate(t: Any?, p: KProperty<*>): Unnamed<StructDefinition> {
        return Unnamed { namespace, name ->
            val asNamespace = namespace + name
            StructDefinition(
                name = name,
                namespace = namespace,
                isInline = false,
                description = "",
                fields = this.fields.map {
                    when (it) {
                        is FieldDefinition -> it
                        is AnonymousField -> it.createDefinition(asNamespace)
                    }
                }
            )
        }
    }
}

open class AnonymousStructBuilder : StructBuilder() {
    protected open var fields = mutableListOf<Field>()

    override operator fun Field.unaryPlus() {
        fields += this
    }

    override fun build(): AnonymousStruct {
        return AnonymousStruct(
            fields = this.fields,
        )
    }
}

@Marker1
fun struct(vararg fields: Field) = AnonymousStruct(fields.asList())

////////////////////////////////////////
