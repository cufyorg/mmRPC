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

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

////////////////////////////////////////

@Serializable
data class MetadataDefinitionUsage(
    val definition: MetadataDefinition,
    val fields: List<FieldDefinitionUsage>,
) {
    fun collect() = sequence {
        yieldAll(definition.collect())
        yieldAll(fields.asSequence().flatMap { it.collect() })
    }
}

class MetadataDefinitionUsageBuilder {
    lateinit var definition: MetadataDefinition
    private val fields = mutableListOf<FieldDefinitionUsage>()

    @JvmName("unaryPlusFieldDefinitionUsage")
    operator fun FieldDefinitionUsage.unaryPlus() {
        fields += this
    }

    @JvmName("unaryPlusIterableFieldDefinitionUsage")
    operator fun Iterable<FieldDefinitionUsage>.unaryPlus() {
        fields += this
    }

    fun build(): MetadataDefinitionUsage {
        return MetadataDefinitionUsage(
            definition = this.definition,
            fields = this.fields.toList(),
        )
    }
}

////////////////////////////////////////

operator fun MetadataDefinition.invoke(
    block: MetadataDefinitionUsageBuilder.() -> Unit = {}
): MetadataDefinitionUsage {
    return MetadataDefinitionUsageBuilder()
        .also { it.definition = this }
        .apply(block)
        .build()
}

////////////////////////////////////////
