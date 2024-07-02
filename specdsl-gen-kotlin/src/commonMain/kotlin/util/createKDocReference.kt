package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createKDocReference(element: ElementDefinition): String {
    if (element.isAnonymous)
        return "`${element.canonicalName.value}`"

    val identifier = when (element) {
        is ConstDefinition -> element.asReferenceName
        is FieldDefinition -> element.asReferenceName

        is MetadataDefinition -> {
            if (element.canonicalName in ctx.nativeElements)
                return "`${element.canonicalName.value}`"

            element.asClassName
        }

        is ScalarDefinition -> {
            if (element.canonicalName in ctx.nativeElements)
                return "`${element.canonicalName.value}`"

            element.asClassName
        }

        is FaultDefinition -> element.asClassName
        is StructDefinition -> element.asClassName
        is UnionDefinition -> element.asClassName
        is InterDefinition -> element.asClassName
        is TupleDefinition -> element.asClassName

        is MetadataParameterDefinition,
        is ProtocolDefinition,
        is RoutineDefinition,
        is ArrayDefinition,
        is OptionalDefinition,
        is EndpointDefinition,
        -> return "`${element.canonicalName.value}`"
    }

    return "[${element.namespace.asClassName}.${identifier}]"
}
