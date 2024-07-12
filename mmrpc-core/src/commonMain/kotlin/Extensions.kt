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

fun infoOf(element: ElementObject) = element.__info__
fun infoOf(element: TypeObject) = element.__info__
fun infoOf(element: EndpointObject) = element.__info__
fun infoOf(element: ConstObject<*>) = element.__info__
fun infoOf(element: FaultObject) = element.__info__
fun infoOf(element: FieldObject<*>) = element.__info__
fun infoOf(element: ProtocolObject) = element.__info__
fun infoOf(element: RoutineObject<*, *>) = element.__info__
fun infoOf(element: HttpEndpointObject) = element.__info__
fun infoOf(element: IframeEndpointObject) = element.__info__
fun infoOf(element: KafkaEndpointObject<*>) = element.__info__
fun infoOf(element: KafkaPublicationEndpointObject<*>) = element.__info__
fun infoOf(element: EnumObject<*>) = element.__info__
fun infoOf(element: ScalarObject<*>) = element.__info__
fun infoOf(element: StructObject) = element.__info__
fun infoOf(element: TupleObject) = element.__info__
