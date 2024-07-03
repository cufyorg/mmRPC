package org.cufy.specdsl.gradle.kotlin

import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.core.*
import org.cufy.specdsl.gen.kotlin.core.endpoint.HttpGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.IframeGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.KafkaGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.KafkaPublicationGen

fun runGenGroups(ctx: GenContext) {
    /* =============== core =============== */

    ConstDefinitionGen(ctx).apply()
    FaultDefinitionGen(ctx).apply()
    ProtocolDefinitionGen(ctx).apply()
    RoutineDefinitionGen(ctx).apply()
    FieldDefinitionGen(ctx).apply()
    MetadataDefinitionGen(ctx).apply()
    ScalarDefinitionGen(ctx).apply()
    StructDefinitionGen(ctx).apply()
    UnionDefinitionGen(ctx).apply()

    /* =============== core.endpoint =============== */

    HttpGen(ctx).apply()
    IframeGen(ctx).apply()
    KafkaGen(ctx).apply()
    KafkaPublicationGen(ctx).apply()
}
