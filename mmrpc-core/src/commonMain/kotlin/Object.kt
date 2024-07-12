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

interface ElementObject {
    val info: ElementInfo
}

interface TypeObject : ElementObject {
    override val info: TypeInfo
}

interface EndpointObject : ElementObject {
    override val info: EndpointInfo
}

// Elements

interface ConstObject<T> : ElementObject {
    override val info: ConstInfo

    val value: T
}

interface FaultObject : ElementObject {
    override val info: FaultInfo
}

interface FieldObject : ElementObject {
    override val info: FieldInfo
}

interface ProtocolObject : ElementObject {
    override val info: ProtocolInfo
}

interface RoutineObject<I, O> : ElementObject {
    override val info: RoutineInfo
}

// Endpoints

interface HttpEndpointObject : EndpointObject {
    override val info: HttpEndpointInfo
}

interface IframeEndpointObject : EndpointObject {
    override val info: IframeEndpointInfo
}

interface KafkaEndpointObject : EndpointObject {
    override val info: KafkaEndpointInfo
}

interface KafkaPublicationEndpointObject : EndpointObject {
    override val info: KafkaPublicationEndpointInfo
}

// Types

interface EnumObject<T> : TypeObject {
    override val info: EnumInfo

    val value: T
}

interface StructObject : TypeObject {
    override val info: StructInfo
}
