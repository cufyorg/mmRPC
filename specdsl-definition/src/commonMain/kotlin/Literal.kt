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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
sealed interface Literal {
    fun contentToString(): String
}

@Serializable
@SerialName("null")
data object NullLiteral : Literal {
    override fun contentToString() = "null"
}

@Marker1
@get:JvmName("literalOrNull")
val Nothing?.literal get() = NullLiteral

@Serializable
@SerialName("boolean")
data class BooleanLiteral(val value: Boolean) : Literal {
    override fun contentToString() = value.toString()
}

@Marker1
val Boolean.literal get() = BooleanLiteral(this)

@Marker1
@get:JvmName("literalOrNull")
val Boolean?.literal get() = this?.literal ?: null.literal

@Serializable
@SerialName("int")
data class IntLiteral(val value: Long) : Literal {
    override fun contentToString() = value.toString()
}

@Marker1
val Int.literal get() = IntLiteral(toLong())

@Marker1
@get:JvmName("literalOrNull")
val Int?.literal get() = this?.literal ?: null.literal

@Marker1
val Long.literal get() = IntLiteral(this)

@Marker1
@get:JvmName("literalOrNull")
val Long?.literal get() = this?.literal ?: null.literal

@Serializable
@SerialName("float")
data class FloatLiteral(val value: Double) : Literal {
    override fun contentToString() = value.toString()
}

@Marker1
val Float.literal get() = FloatLiteral(toDouble())

@Marker1
@get:JvmName("literalOrNull")
val Float?.literal get() = this?.literal ?: null.literal

@Marker1
val Double.literal get() = FloatLiteral(this)

@Marker1
@get:JvmName("literalOrNull")
val Double?.literal get() = this?.literal ?: null.literal

@Serializable
@SerialName("string")
data class StringLiteral(val value: String) : Literal {
    override fun contentToString() = "\"$value\""
}

@Marker1
val String.literal get() = StringLiteral(this)

@Marker1
@get:JvmName("literalOrNull")
val String?.literal get() = this?.literal ?: null.literal

@Serializable
@SerialName("tuple")
data class TupleLiteral(val value: List<Literal>) : Literal {
    override fun contentToString() = value.joinToString(", ", "[", "]") { it.contentToString() }
}

@Marker1
fun literal(vararg value: Literal) =
    TupleLiteral(value.asList())
