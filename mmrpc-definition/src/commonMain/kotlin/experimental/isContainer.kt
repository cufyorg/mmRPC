package org.cufy.mmrpc.experimental

import org.cufy.mmrpc.*

fun ElementDefinition.isContainer(): Boolean {
    return when (this) {
        is ArrayDefinition,
        is ConstDefinition,
        is FaultDefinition,
        is FieldDefinition,
        is MapDefinition,
        is OptionalDefinition,
        is ScalarDefinition,
        is TupleDefinition,
        is UnionDefinition,
        -> false

        is EnumDefinition,
        is MetadataDefinition,
        is ProtocolDefinition,
        is RoutineDefinition,
        is StructDefinition,
        is TraitDefinition,
        -> true
    }
}
