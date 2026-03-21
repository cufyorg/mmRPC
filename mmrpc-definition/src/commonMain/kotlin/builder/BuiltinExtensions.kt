@file:Suppress("FunctionName")

package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

@Marker3
context(ctx: MetadataUsageContainerBuilder)
fun Experimental(message: String) {
    +builtin.Experimental {
        +builtin.Experimental__message(message.literal)
    }
}

@Marker3
context(ctx: MetadataUsageContainerBuilder)
fun Deprecated(message: String) {
    +builtin.Deprecated {
        +builtin.Deprecated__message(message.literal)
    }
}

@Marker3
context(ctx: MetadataUsageContainerBuilder)
fun Contract(value: String) {
    +builtin.Contract {
        +builtin.Contract__value(value.literal)
    }
}

private val extTypeArray = ext { type: TypeDefinition -> array(type) }
    .get(name = "array")

@Marker2
val TypeDefinition.array: ArrayDefinition
    get() = extTypeArray(this)

@Marker2
val Unnamed<TypeDefinition>.array: Unnamed<ArrayDefinition>
    get() = extTypeArray(this)

private val extTypeOptional = ext { type: TypeDefinition -> optional(type) }
    .get(name = "optional")

@Marker2
val TypeDefinition.optional: OptionalDefinition
    get() = extTypeOptional(this)

@Marker2
val Unnamed<TypeDefinition>.optional: Unnamed<OptionalDefinition>
    get() = extTypeOptional(this)
