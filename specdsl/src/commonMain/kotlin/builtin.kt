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

/**
 * Namespace to be used for defining builtins.
 */
@Suppress("ClassName")
object builtin : NamespaceObject() {
    val Nothing by nativeScalar { +"builtin nothing (the type of null)" }
    val NULL by const("null", Nothing) { +"builtin null" }
    val String by nativeScalar { +"builtin string" }
    val Boolean by nativeScalar { +"builtin boolean" }
    val TRUE by const("true", Boolean) { +"builtin true" }
    val FALSE by const("false", Boolean) { +"builtin false" }
    val Int32 by nativeScalar { +"builtin 32bit int" }
    val UInt32 by nativeScalar { +"builtin unsigned 32bit int" }
    val Int64 by nativeScalar { +"builtin 64bit int" }
    val UInt64 by nativeScalar { +"builtin unsigned 64bit int" }
    val Float32 by nativeScalar { +"builtin 32bit float" }
    val Float64 by nativeScalar { +"builtin 64bit float" }

    val Deprecated__message by metadata_param { +"The deprecation message" }
    val Deprecated by nativeMetadata(Deprecated__message)

    /**
     * Namespace to be used for defining builtin faults.
     */
    object fault : NamespaceObject(this)

    /**
     * Namespace to be used for defining builtin props.
     */
    object prop : NamespaceObject(this)

    /**
     * Namespace to be used for defining builtin hydration props.
     */
    object hydration : NamespaceObject(this)

    /**
     * Namespace to be used for defining builtin serialization objects.
     */
    object serial : NamespaceObject(this)

    /**
     * Namespace to be used for defining builtin tokens.
     */
    object token : NamespaceObject(this)
}
