@file:Suppress("FunctionName")

package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.TupleLiteral
import org.cufy.mmrpc.builtin
import org.cufy.mmrpc.literal

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
fun Contract(value: List<String>) {
    +builtin.Contract {
        +builtin.Contract__value(TupleLiteral(value.map { it.literal }))
    }
}

@Marker3
context(ctx: MetadataUsageContainerBuilder)
fun Contract(vararg value: String) {
    +builtin.Contract {
        +builtin.Contract__value(TupleLiteral(value.map { it.literal }))
    }
}
