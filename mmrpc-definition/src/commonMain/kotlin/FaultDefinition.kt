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

////////////////////////////////////////

@Serializable
@SerialName("fault")
data class FaultDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
) : ElementDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
    }
}

open class FaultDefinitionBuilder :
    ElementDefinitionBuilder() {
    override fun build(): FaultDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return FaultDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
        )
    }
}

@Marker2
fun fault(
    block: FaultDefinitionBuilder.() -> Unit = {},
): Unnamed<FaultDefinition> {
    return Unnamed { namespace, name ->
        FaultDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
val fault = fault()

////////////////////////////////////////
