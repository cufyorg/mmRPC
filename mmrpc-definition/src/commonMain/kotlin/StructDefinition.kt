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
package org.cufy.mmrpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

////////////////////////////////////////

@Serializable
@SerialName("struct")
data class StructDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val fields: List<FieldDefinition> = emptyList(),
) : TypeDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(fields.asSequence().flatMap { it.collect() })
    }
}

open class StructDefinitionBuilder :
    FieldDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    protected open var fields = mutableListOf<Unnamed<FieldDefinition>>()

////////////////////////////////////////

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedFieldDefinition")
    override operator fun Unnamed<FieldDefinition>.unaryPlus() {
        fields += this
    }

////////////////////////////////////////

    override fun build(): StructDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return StructDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            fields = this.fields.mapIndexed { i, it ->
                it.get(canonicalName, name = "field$i")
            },
        )
    }
}

@Marker2
fun struct(
    block: StructDefinitionBuilder.() -> Unit = {},
): Unnamed<StructDefinition> {
    return Unnamed { namespace, name ->
        StructDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
val struct = struct()

@Marker2
fun struct(
    vararg fields: FieldDefinition,
    block: StructDefinitionBuilder.() -> Unit = {},
): Unnamed<StructDefinition> {
    return struct { +fields.asList(); block() }
}

@Marker2
fun struct(
    vararg fields: Unnamed<FieldDefinition>,
    block: StructDefinitionBuilder.() -> Unit = {},
): Unnamed<StructDefinition> {
    return struct { +fields.asList(); block() }
}

////////////////////////////////////////
