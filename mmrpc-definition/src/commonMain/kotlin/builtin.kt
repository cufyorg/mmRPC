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

/**
 * Namespace to be used for defining builtins.
 */
@Suppress("ClassName")
object builtin : NamespaceObject() {
    val Any by scalar { +"Everything extends Any" }
    val NULL by const(null.literal) { +"builtin null" }
    val String by scalar { +"builtin string" }
    val Boolean by scalar { +"builtin boolean" }
    val TRUE by const(true.literal) { +"builtin true" }
    val FALSE by const(false.literal) { +"builtin false" }
    val Int32 by scalar { +"builtin 32bit int" }
    val UInt32 by scalar { +"builtin unsigned 32bit int" }
    val Int64 by scalar { +"builtin 64bit int" }
    val UInt64 by scalar { +"builtin unsigned 64bit int" }
    val Float32 by scalar { +"builtin 32bit float" }
    val Float64 by scalar { +"builtin 64bit float" }

    val Deprecated__message by prop(String) { +"The deprecation message" }
    val Deprecated by metadata(Deprecated__message)

    val Experimental__message by prop(String)
    val Experimental by metadata(Experimental__message)

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
