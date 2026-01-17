package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.gen.*
import org.cufy.mmrpc.gen.kotlin.gen.integ.doProtocolCustomGen
import org.cufy.mmrpc.gen.kotlin.gen.integ.doProtocolHttpGen
import org.cufy.mmrpc.gen.kotlin.gen.integ.doProtocolKafkaGen

private const val GENERATED_FILE_NOTICE = """
This is an automatically generated file.
Modification to this file WILL be lost everytime
the code generation task is executed

This file was generated with mmrpc-gen-kotlin via
gradle plugin: org.cufy.mmrpc version: UNKNOWN
"""

fun Context.solve(): ResultList {
    val failScope = FailScope()
    val nodes = context(failScope) { initStage() }
    val files = context(failScope) { finalStage(nodes) }
    return ResultList(
        files = files,
        fails = failScope.caught,
    )
}

context(ctx: Context, _: FailScope)
private fun initStage(): NodeList {
    val stage = InitStage()
    stage.apply {
        doArrayDefinitionGen()
        doMapDefinitionGen()
        doConstDefinitionGen()
        doEnumDefinitionGen()
        doFaultDefinitionGen()
        doProtocolDefinitionGen()
        doRoutineDefinitionGen()
        doMetadataDefinitionGen()
        doScalarDefinitionGen()
        doTraitDefinitionGen()
        doStructDefinitionGen()
        doTupleDefinitionGen()
        doUnionDefinitionGen()

        doProtocolHttpGen()
        doProtocolKafkaGen()
        doProtocolCustomGen()
    }
    sort(stage.injections)
    return NodeList(
        emissions = stage.emissions,
        injections = stage.injections,
    )
}

context(_: Context, _: FailScope)
private fun finalStage(nodes: NodeList): List<FileSpec> {
    val stage = FinalStage(nodes)

    for (node in nodes.emissions) {
        node.emission(stage)
    }

    for (node in nodes.injections) {
        if (node in stage.injected) continue
        catch(node.declares + node.target) {
            node.fallback(stage)
        }
        stage.injected += node
    }

    for (node in nodes.injections) {
        if (node in stage.injected) continue
        catch(node.declares + node.target) {
            fail("Uninjected node: $node")
        }
    }

    return stage.files.values.map {
        it.addFileComment(GENERATED_FILE_NOTICE)
        it.build()
    }
}
