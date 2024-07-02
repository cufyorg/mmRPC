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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
@SerialName("metadata")
data class MetadataDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("metadata_parameters")
    val metadataParameters: List<MetadataParameterDefinition> = emptyList(),
) : ElementDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous@)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(metadataParameters.asSequence().flatMap { it.collect() })
    }
}

open class MetadataDefinitionBuilder :
    MetadataParameterDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = MetadataDefinition.ANONYMOUS_NAME

    protected open var metadataParametersUnnamed = mutableListOf<Unnamed<MetadataParameterDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedMetadataParameterDefinition")
    override operator fun Unnamed<MetadataParameterDefinition>.unaryPlus() {
        metadataParametersUnnamed += this
    }

    override fun build(): MetadataDefinition {
        val asNamespace = this.namespace.value + this.name
        return MetadataDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            metadataParameters = this.metadataParametersUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "parameter$i")
            },
        )
    }
}

@Marker1
fun metadata(
    block: MetadataDefinitionBuilder.() -> Unit = {}
): Unnamed<MetadataDefinition> {
    return Unnamed { namespace, name, isInline ->
        MetadataDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = isInline }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val metadata = metadata()

@Marker1
fun metadata(
    vararg parameters: MetadataParameterDefinition,
    block: MetadataDefinitionBuilder.() -> Unit = {}
): Unnamed<MetadataDefinition> {
    return metadata { +parameters.asList(); block() }
}

@Marker1
fun metadata(
    vararg parameters: Unnamed<MetadataParameterDefinition>,
    block: MetadataDefinitionBuilder.() -> Unit = {}
): Unnamed<MetadataDefinition> {
    return metadata { +parameters.asList(); block() }
}

////////////////////////////////////////
