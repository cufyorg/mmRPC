package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG
import org.cufy.mmrpc.internal.asAnonChildOf

////////////////////////////////////////

typealias ProtocolDefinitionBlock = context(ProtocolDefinitionBuilder) () -> Unit

@Marker2
class ProtocolDefinitionBuilder :
    RoutineDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val routines = mutableListOf<Unnamed<RoutineDefinition>>()

    override fun addRoutineDefinition(value: Unnamed<RoutineDefinition>) {
        routines += value
    }

    fun build(): ProtocolDefinition {
        val cn = buildCanonicalName()
        return ProtocolDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            routines = this.routines.mapIndexed { i, it ->
                it.asAnonChildOf(cn, "routine", i)
            }
        )
    }
}

////////////////////////////////////////

@Marker2
val protocol = protocol()

@Marker2
fun protocol(
    block: ProtocolDefinitionBlock = {},
) = Unnamed { ns, name ->
    ProtocolDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun protocol(
    vararg routines: RoutineDefinition,
    block: ProtocolDefinitionBlock = {},
) = protocol {
    +routines.asList()
    block()
}

@Marker2
@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
fun protocol(
    vararg routines: Unnamed<RoutineDefinition>,
    block: ProtocolDefinitionBlock = {},
) = protocol {
    +routines.asList()
    block()
}

////////////////////////////////////////
