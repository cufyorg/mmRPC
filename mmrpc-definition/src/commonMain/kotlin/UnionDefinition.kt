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
@SerialName("union")
data class UnionDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("union_discriminator")
    val unionDiscriminator: String = "type",
    @SerialName("union_types")
    val unionTypes: List<StructDefinition>,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous|)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(unionTypes.asSequence().flatMap { it.collect() })
    }
}

open class UnionDefinitionBuilder :
    StructDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = UnionDefinition.ANONYMOUS_NAME

    open var discriminator: String = "type"

    protected open val unionTypesUnnamed = mutableListOf<Unnamed<StructDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedStructDefinition")
    override operator fun Unnamed<StructDefinition>.unaryPlus() {
        unionTypesUnnamed += this
    }

    override fun build(): UnionDefinition {
        val asNamespace = this.namespace.value + this.name
        return UnionDefinition(
            name = this.name,
            namespace = this.namespace.value,
            description = this.description,
            metadata = this.metadata.toList(),
            unionDiscriminator = this.discriminator,
            unionTypes = this.unionTypesUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "type$i")
            },
        )
    }
}

@Marker2
fun union(
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return Unnamed { namespace, name ->
        UnionDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun union(
    vararg types: StructDefinition,
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return union { +types.asList(); block() }
}

@Marker2
fun union(
    vararg types: Unnamed<StructDefinition>,
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return union { +types.asList(); block() }
}

////////////////////////////////////////
