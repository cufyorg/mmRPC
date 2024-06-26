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

import kotlinx.serialization.Serializable

////////////////////////////////////////

@Serializable
data class MetadataParameter(
    val definition: MetadataParameterDefinition,
    val value: String,
) {
    fun collect() = sequence {
        yieldAll(definition.collect())
    }
}

open class MetadataParameterBuilder {
    lateinit var definition: MetadataParameterDefinition
    lateinit var value: String

    fun build(): MetadataParameter {
        return MetadataParameter(
            definition = this.definition,
            value = this.value,
        )
    }
}

////////////////////////////////////////

operator fun MetadataParameterDefinition.invoke(
    value: String,
    block: MetadataParameterBuilder.() -> Unit = {}
): MetadataParameter {
    return MetadataParameterBuilder()
        .also { it.definition = this }
        .also { it.value = value }
        .apply(block)
        .build()
}

////////////////////////////////////////
