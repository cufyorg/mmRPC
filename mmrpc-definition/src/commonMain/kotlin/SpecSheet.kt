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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(SpecSheet.Serializer::class)
class SpecSheet {
    companion object {
        internal fun create0(elements: Set<ElementDefinition>): SpecSheet {
            return SpecSheet().also { it.elements = elements }
        }
    }

    class Serializer : KSerializer<SpecSheet> {
        private val delegate = SetSerializer(ElementDefinition.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("SpecSheet", delegate.descriptor)

        override fun serialize(encoder: Encoder, value: SpecSheet) =
            encoder.encodeSerializableValue(delegate, value.elements)

        override fun deserialize(decoder: Decoder) =
            SpecSheet(decoder.decodeSerializableValue(delegate))
    }

    var elements: Set<ElementDefinition> = emptySet()
        private set

    constructor()

    constructor(vararg elements: ElementDefinition) {
        this.elements = elements
            .asSequence()
            .flatMap { it.collect() }
            .distinct()
            .toSet()
    }

    constructor(elements: Iterable<ElementDefinition>) {
        this.elements = elements
            .asSequence()
            .flatMap { it.collect() }
            .distinct()
            .toSet()
    }

    override fun equals(other: Any?) =
        other is SpecSheet && other.elements == this.elements

    override fun hashCode() =
        this.elements.hashCode()

    override fun toString() =
        "SpecSheet(${this.elements.joinToString(", ")})"

    operator fun plus(element: ElementDefinition) =
        if (element in this.elements) this else create0(this.elements + element)

    operator fun plus(elements: Iterable<ElementDefinition>) =
        SpecSheet(this.elements + elements)

    operator fun plus(specSheet: SpecSheet) =
        SpecSheet(this.elements + specSheet.elements)

    operator fun minus(element: ElementDefinition) =
        if (element !in this.elements) this else create0(this.elements - element)

    operator fun minus(elements: Iterable<ElementDefinition>) =
        @Suppress("ConvertArgumentToSet")
        create0(this.elements - elements)

    operator fun minus(specSheet: SpecSheet) =
        create0(this.elements - specSheet.elements)

    fun collectChildren() = elements.asSequence()

    operator fun contains(element: ElementDefinition): Boolean {
        return element in this.elements
    }
}
