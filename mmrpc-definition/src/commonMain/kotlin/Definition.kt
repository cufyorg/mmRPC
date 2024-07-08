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

@Serializable
sealed class ElementDefinition {
    abstract val name: String
    abstract val namespace: Namespace

    @SerialName("is_inline")
    abstract val isInline: Boolean
    abstract val description: String
    abstract val metadata: List<MetadataDefinitionUsage>

    val canonicalName by lazy { CanonicalName(namespace, name) }
    val isAnonymous by lazy { namespace.isAnonymous || Namespace.isAnonymousSegment(name) }

    val asNamespace by lazy { namespace + name }

    fun collect() = sequenceOf(this) + collectChildren()

    abstract fun collectChildren(): Sequence<ElementDefinition>
}

@Serializable
sealed class TypeDefinition : ElementDefinition()

@Serializable
sealed class EndpointDefinition : ElementDefinition()
