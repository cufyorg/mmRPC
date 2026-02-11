package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

@Marker2
fun propList(block: context(FieldDefinitionContainerBuilder) () -> Unit): List<Unnamed<FieldDefinition>> =
    buildList { block { add(it) } }

@Marker2
fun faultList(block: context(FaultDefinitionContainerBuilder) () -> Unit): List<Unnamed<FaultDefinition>> =
    buildList { block { add(it) } }

@Marker2
fun structList(block: context(StructDefinitionContainerBuilder) () -> Unit): List<Unnamed<StructDefinition>> =
    buildList { block { add(it) } }

@Marker2
fun traitList(block: context(TraitDefinitionContainerBuilder) () -> Unit): List<Unnamed<TraitDefinition>> =
    buildList { block { add(it) } }

@Marker2
fun markdown(block: context(MarkdownContainerBuilder) () -> Unit): String =
    buildList { block { add(it) } }.joinToString("\n")
