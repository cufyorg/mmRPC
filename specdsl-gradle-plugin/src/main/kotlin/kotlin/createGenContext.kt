package org.cufy.specdsl.gradle.kotlin

import com.squareup.kotlinpoet.ClassName
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.SpecSheet
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenFeature

fun createGenContext(
    specSheet: SpecSheet,
    packageName: String,
    classes: Map<String, String>,
    defaultScalarClass: String?,
    nativeElements: Set<String>,
    features: Set<GenFeature>,
): GenContext {
    return GenContext(
        pkg = packageName,
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
