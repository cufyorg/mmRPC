package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.*

// asReferenceName

val ConstDefinition.asReferenceName: String
    get() = name

val FieldDefinition.asReferenceName: String
    get() = name.trainCase()

val Namespace.asReferenceName: String
    get() = name.replace(':', '_')

val Namespace.asQualifiedReferenceName: String
    get() = segments.joinToString(".") {
        it.replace(':', '_')
    }

// asClassName

val Namespace.asClassName: String
    get() = segments.joinToString("_") {
        it.replace(':', '_')
    }

val FaultDefinition.asClassName: String
    get() = name

val RoutineDefinition.asClassName: String
    get() = name.replace(":", "_")

val MetadataDefinition.asClassName: String
    get() = name

val ScalarDefinition.asClassName: String
    get() = name

val StructDefinition.asClassName: String
    get() = name

val UnionDefinition.asClassName: String
    get() = name

val InterDefinition.asClassName: String
    get() = name

val TupleDefinition.asClassName: String
    get() = name
