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

abstract class ElementObject {
    abstract val info: ElementInfo
}

abstract class TypeObject : ElementObject() {
    abstract override val info: TypeInfo
}

abstract class EndpointObject : ElementObject() {
    abstract override val info: EndpointInfo
}

// Elements

abstract class FaultObject : ElementObject() {
    abstract override val info: FaultInfo
}

abstract class FieldObject : ElementObject() {
    abstract override val info: FieldInfo
}

abstract class ProtocolObject : ElementObject() {
    abstract override val info: ProtocolInfo
}

abstract class RoutineObject<I : StructObject, O : StructObject> : ElementObject() {
    abstract override val info: RoutineInfo
}

// Endpoints

abstract class HttpEndpointObject : EndpointObject() {
    abstract override val info: HttpEndpointInfo
}

abstract class IframeEndpointObject : EndpointObject() {
    abstract override val info: IframeEndpointInfo
}

abstract class KafkaEndpointObject : EndpointObject() {
    abstract override val info: KafkaEndpointInfo
}

abstract class KafkaPublicationEndpointObject : EndpointObject() {
    abstract override val info: KafkaPublicationEndpointInfo
}

// Types

abstract class ConstObject<T> : TypeObject() {
    abstract override val info: ConstInfo

    abstract val value: T
}

abstract class StructObject : TypeObject() {
    abstract override val info: StructInfo
}
