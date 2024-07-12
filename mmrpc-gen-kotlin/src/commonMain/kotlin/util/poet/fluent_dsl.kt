package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

// ParameterSpec.builder

fun parameterSpec(name: String, type: TypeName, block: ParameterSpec.Builder.() -> Unit = {}): ParameterSpec {
    return ParameterSpec.builder(name, type).apply(block).build()
}

fun parameterSpec(name: String, type: KClass<*>, block: ParameterSpec.Builder.() -> Unit = {}): ParameterSpec {
    return ParameterSpec.builder(name, type).apply(block).build()
}

// AnnotationSpec.builder

fun annotationSpec(type: ClassName, block: AnnotationSpec.Builder.() -> Unit = {}): AnnotationSpec {
    return AnnotationSpec.builder(type).apply(block).build()
}

fun annotationSpec(type: KClass<out Annotation>, block: AnnotationSpec.Builder.() -> Unit = {}): AnnotationSpec {
    return AnnotationSpec.builder(type).apply(block).build()
}

// FunSpec.constructorBuilder

fun constructorSpec(block: FunSpec.Builder.() -> Unit = {}): FunSpec {
    return FunSpec.constructorBuilder().apply(block).build()
}

// FunSpec.builder

fun funSpec(name: String, block: FunSpec.Builder.() -> Unit = {}): FunSpec {
    return FunSpec.builder(name).apply(block).build()
}

// FunSpec.getterBuilder

fun getterSpec(block: FunSpec.Builder.() -> Unit = {}): FunSpec {
    return FunSpec.getterBuilder().apply(block).build()
}

// PropertySpec.builder

fun propertySpec(name: String, type: TypeName, block: PropertySpec.Builder.() -> Unit = {}): PropertySpec {
    return PropertySpec.builder(name, type).apply(block).build()
}

fun propertySpec(name: String, type: KClass<*>, block: PropertySpec.Builder.() -> Unit = {}): PropertySpec {
    return PropertySpec.builder(name, type).apply(block).build()
}

// TypeSpec.classBuilder

fun classSpec(name: String, block: TypeSpec.Builder.() -> Unit = {}): TypeSpec {
    return TypeSpec.classBuilder(name).apply(block).build()
}

// TypeSpec.anonymousClassBuilder

fun anonymousClassSpec(block: TypeSpec.Builder.() -> Unit = {}): TypeSpec {
    return TypeSpec.anonymousClassBuilder().apply(block).build()
}

// TypeSpec.companionObjectBuilder

fun companionObjectSpec(block: TypeSpec.Builder.() -> Unit = {}): TypeSpec {
    return TypeSpec.companionObjectBuilder().apply(block).build()
}
