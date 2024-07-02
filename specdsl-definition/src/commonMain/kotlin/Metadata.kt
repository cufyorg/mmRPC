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
import kotlin.jvm.JvmName

////////////////////////////////////////

@Serializable
data class Metadata(
    val definition: MetadataDefinition,
    val parameters: List<MetadataParameter>,
) {
    fun collect() = sequence {
        yieldAll(definition.collect())
        yieldAll(parameters.asSequence().flatMap { it.collect() })
    }
}

open class MetadataBuilder {
    lateinit var definition: MetadataDefinition

    protected open val parameters = mutableListOf<MetadataParameter>()

    @JvmName("unaryPlusMetadataParameter")
    operator fun MetadataParameter.unaryPlus() {
        parameters += this
    }

    @JvmName("unaryPlusIterableMetadataParameter")
    operator fun Iterable<MetadataParameter>.unaryPlus() {
        parameters += this
    }

    fun build(): Metadata {
        return Metadata(
            definition = this.definition,
            parameters = this.parameters.toList(),
        )
    }
}

////////////////////////////////////////

operator fun MetadataDefinition.invoke(
    block: MetadataBuilder.() -> Unit = {}
): Metadata {
    return MetadataBuilder()
        .also { it.definition = this }
        .apply(block)
        .build()
}

////////////////////////////////////////
