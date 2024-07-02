package org.cufy.specdsl.gradle.kotlin

import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.core.*
import org.cufy.specdsl.gen.kotlin.core.endpoint.HttpGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.IframeGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.KafkaGen
import org.cufy.specdsl.gen.kotlin.core.endpoint.KafkaPublicationGen

fun runGenGroups(ctx: GenContext) {
    /* =============== core =============== */

    ConstDefinitionGen(ctx).run {
        generateConstants()
    }
    FaultDefinitionGen(ctx).run {
        generateClasses()
    }
    FieldDefinitionGen(ctx).run {
        generateConstants()
    }
    MetadataDefinitionGen(ctx).run {
        generateClasses()
    }
    NamespaceGen(ctx).run {
        generateAccessors()
    }
    ScalarDefinitionGen(ctx).run {
        generateClasses()
    }
    StructDefinitionGen(ctx).run {
        generateClasses()
    }
    UnionDefinitionGen(ctx).run {
        generateClasses()
    }

    /* =============== core.endpoint =============== */

    HttpGen(ctx).run {
        generateConstants()
    }
    IframeGen(ctx).run {
        generateConstants()
    }
    KafkaGen(ctx).run {
        generateConstants()
    }
    KafkaPublicationGen(ctx).run {
        generateConstants()
    }
}
