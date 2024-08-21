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
@file:Suppress("PropertyName")

package org.cufy.mmrpc

//

interface ElementObject {
    val __info__: ElementInfo
}

interface TypeObject : ElementObject {
    override val __info__: TypeInfo
}

interface EndpointObject : ElementObject {
    override val __info__: EndpointInfo
}

// Elements

/**
 * @param T [ConstInfo.type]
 * @property value [ConstInfo.value]
 */
interface ConstObject<T> : ElementObject {
    override val __info__: ConstInfo

    val value: T
}

interface FaultObject : ElementObject {
    override val __info__: FaultInfo
}

/**
 * @param T [FieldInfo.type]
 * @property default [FieldInfo.default]
 */
interface FieldObject<T> : ElementObject {
    override val __info__: FieldInfo
}

interface ProtocolObject : ElementObject {
    override val __info__: ProtocolInfo
}

/**
 * @param I [RoutineInfo.input]
 * @param O [RoutineInfo.output]
 */
interface RoutineObject<I, O> : ElementObject {
    override val __info__: RoutineInfo
}

// Endpoints

interface HttpEndpointObject : EndpointObject {
    override val __info__: HttpEndpointInfo
}

interface IframeEndpointObject : EndpointObject {
    override val __info__: IframeEndpointInfo
}

interface KafkaEndpointObject : EndpointObject {
    override val __info__: KafkaEndpointInfo
}

interface KafkaPublicationEndpointObject : EndpointObject {
    override val __info__: KafkaPublicationEndpointInfo
}

// Types

/**
 * @param T [EnumInfo.type]
 * @property value [EnumInfo.entries]
 */
interface EnumObject<T> : TypeObject {
    override val __info__: EnumInfo

    val value: T
}

/**
 * @param N native type
 * @property value the wrapped native value
 */
interface ScalarObject<N> : TypeObject {
    override val __info__: ScalarInfo

    val value: N
}

interface StructObject : TypeObject {
    override val __info__: StructInfo
}

interface InterObject : TypeObject {
    override val __info__: InterInfo
}

interface TupleObject : TypeObject {
    override val __info__: TupleInfo

    fun toList(): List<Any?>
}
