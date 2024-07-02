package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("routine")
data class CompactRoutineDefinition(
    override val name: String = RoutineDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("routine_endpoints.ref")
    val routineEndpoints: List<CanonicalName> = emptyList(),
    @SerialName("routine_fault_union.ref")
    val routineFaultUnion: List<CanonicalName> = emptyList(),
    @SerialName("routine_input.ref")
    val routineInput: CanonicalName,
    @SerialName("routine_output.ref")
    val routineOutput: CanonicalName,
) : CompactElementDefinition

fun RoutineDefinition.toCompact(): CompactRoutineDefinition {
    return CompactRoutineDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        routineEndpoints = this.routineEndpoints
            .map { it.canonicalName },
        routineFaultUnion = this.routineFaultUnion
            .map { it.canonicalName },
        routineInput = this.routineInput.canonicalName,
        routineOutput = this.routineOutput.canonicalName,
    )
}

fun CompactRoutineDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> RoutineDefinition? {
    return it@{
        RoutineDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            routineEndpoints = this.routineEndpoints.map {
                val item = onLookup(it) ?: return@it null
                require(item is EndpointDefinition) {
                    "routine_endpoints.ref must point to a EndpointDefinition"
                }
                item
            },
            routineFaultUnion = this.routineFaultUnion.map {
                val item = onLookup(it) ?: return@it null
                require(item is FaultDefinition) {
                    "routine_fault_union.ref must point to a FaultDefinition"
                }
                item
            },
            routineInput = this.routineInput.let {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "routine_input.ref must point to a StructDefinition"
                }
                item
            },
            routineOutput = this.routineOutput.let {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "routine_output.ref must point to a StructDefinition"
                }
                item
            },
        )
    }
}
