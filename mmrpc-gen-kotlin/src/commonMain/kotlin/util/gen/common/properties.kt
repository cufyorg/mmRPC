package org.cufy.mmrpc.gen.kotlin.util.gen.common

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.F_OBJECT_INFO
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_INFO
import org.cufy.mmrpc.gen.kotlin.util.gen.debugRequireGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.refOfINFOOrCreateInfo
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createInfo
import org.cufy.mmrpc.gen.kotlin.util.infoClass
import org.cufy.mmrpc.gen.kotlin.util.poet.getterSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

private const val TAG = "createStaticInfoProperty"

@Marker3
fun GenScope.createStaticInfoProperty(element: ElementDefinition): PropertySpec {
    debugRequireGeneratedClass(TAG, element)
    return propertySpec(F_STATIC_INFO, element.infoClass) {
        initializer("\n%L", createInfo(element))
    }
}

@Marker3
fun GenScope.createDelegateStaticInfoProperty(element: ElementDefinition): PropertySpec {
    return propertySpec(F_STATIC_INFO, element.infoClass) {
        initializer("\n%L", refOfINFOOrCreateInfo(element))
    }
}

@Marker3
fun GenScope.createOverrideObjectInfoProperty(element: ElementDefinition): PropertySpec {
    return propertySpec(F_OBJECT_INFO, element.infoClass) {
        val getter = getterSpec {
            addStatement("return %L", F_STATIC_INFO)
        }

        addModifiers(KModifier.OVERRIDE)
        getter(getter)
    }
}
