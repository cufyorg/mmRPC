package org.cufy.mmrpc

fun ElementDefinition.collect(): Sequence<ElementDefinition> {
    return sequenceOf(this) + collectChildren()
}

fun FieldUsage.collect(): Sequence<ElementDefinition> {
    return sequence {
        yieldAll(definition.collect())
    }
}

fun MetadataUsage.collect(): Sequence<ElementDefinition> {
    return sequence {
        yieldAll(definition.collect())
        yieldAll(fields.asSequence().flatMap { it.collect() })
    }
}

private fun ElementDefinition.collectChildren(): Sequence<ElementDefinition> {
    return when (this) {
        is ConstDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
        }

        is FaultDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
        }

        is FieldDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
        }

        is MetadataDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(fields.asSequence().flatMap { it.collect() })
        }

        is ProtocolDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(routines.asSequence().flatMap { it.collect() })
        }

        is RoutineDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(faults.asSequence().flatMap { it.collect() })
            yieldAll(input.collect())
            yieldAll(output.collect())
        }

        is ArrayDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
        }

        is MapDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
        }

        is EnumDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
            yieldAll(entries.asSequence().flatMap { it.collect() })
        }

        is InterDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(types.asSequence().flatMap { it.collect() })
        }

        is OptionalDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(type.collect())
        }

        is ScalarDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            type?.let { yieldAll(it.collect()) }
        }

        is TraitDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(traits.asSequence().flatMap { it.collect() })
            yieldAll(fields.asSequence().flatMap { it.collect() })
        }

        is StructDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(traits.asSequence().flatMap { it.collect() })
            yieldAll(fields.asSequence().flatMap { it.collect() })
        }

        is TupleDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(types.asSequence().flatMap { it.collect() })
        }

        is UnionDefinition -> sequence {
            yieldAll(metadata.asSequence().flatMap { it.collect() })
            yieldAll(types.asSequence().flatMap { it.collect() })
        }
    }
}
