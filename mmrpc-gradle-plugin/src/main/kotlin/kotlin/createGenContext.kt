package org.cufy.mmrpc.gradle.kotlin

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.SpecSheet
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging

fun createGenContext(
    specSheet: SpecSheet,
    packageName: String,
    packaging: GenPackaging,
    classes: Map<String, String>,
    defaultScalarClass: String?,
    nativeElements: Set<String>,
    features: Set<GenFeature>,
): GenContext {
    return GenContext(
        pkg = packageName,
        packaging = packaging,
        specSheet = specSheet,
        classes = classes
            .asSequence()
            .map { CanonicalName(it.key) to ClassName.bestGuess(it.value) }
            .toMap(),
        defaultScalarClass = defaultScalarClass
            ?.let { ClassName.bestGuess(it) },
        nativeElements = nativeElements
            .asSequence()
            .map { CanonicalName(it) }
            .toSet(),
        features = features,
    )
}
