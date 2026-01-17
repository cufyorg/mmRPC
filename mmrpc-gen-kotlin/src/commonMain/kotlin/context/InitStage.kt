package org.cufy.mmrpc.gen.kotlin.context

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.gen.kotlin.EmitNode
import org.cufy.mmrpc.gen.kotlin.InjectNode
import org.cufy.mmrpc.gen.kotlin.InjectScope
import org.cufy.mmrpc.gen.kotlin.common.toPackageName
import kotlin.reflect.full.isSubclassOf

class InitStage {
    val emissions = mutableListOf<EmitNode>()
    val injections = mutableListOf<InjectNode<*>>()
}

@InjectScope
context(stage: InitStage)
inline fun <reified T : Any> inject(
    dummy: Unit = Unit,
    target: CanonicalName?,
    declares: List<CanonicalName> = emptyList(),
    noinline injection: context(FinalStage) T.() -> Unit,
) {
    val e = RuntimeException("Injection failed")
    stage.injections += InjectNode(
        type = T::class,
        target = target,
        declares = declares,
        injection = injection,
        fallback = {
            fail("Injected code wasn't applied to cn: ${target?.value}", e)
        },
    )
}

@InjectScope
context(stage: InitStage, _: Context)
inline fun <reified T : Any> injectOrToplevel(
    dummy: Unit = Unit,
    target: CanonicalName?,
    declares: List<CanonicalName> = emptyList(),
    noinline injection: context(FinalStage) T.() -> Unit,
) {
    require(FileSpec.Builder::class.isSubclassOf(T::class)) {
        "injectOrToplevel type constraint can't be satisfied"
    }
    stage.injections += InjectNode(
        type = T::class,
        target = target,
        declares = declares,
        injection = injection,
        fallback = {
            add(pkg = target.toPackageName(), name = "Declarations") {
                @Suppress("UNCHECKED_CAST")
                (injection as context(FinalStage) FileSpec.Builder.() -> Unit)()
            }
        },
    )
}

@InjectScope
context(stage: InitStage, _: Context)
fun declareType(
    dummy: Unit = Unit,
    target: CanonicalName?,
    declares: List<CanonicalName> = emptyList(),
    block: context(FinalStage) () -> TypeSpec
) {
    stage.injections += InjectNode(
        type = TypeSpecHolder.Builder::class,
        target = target,
        declares = declares,
        injection = { addType(block()) },
        fallback = {
            val type = block()
            add(pkg = target.toPackageName(), name = type.name!!) {
                addType(type)
            }
        },
    )
}

@InjectScope
context(stage: InitStage, _: Context)
fun toplevel(
    dummy: Unit = Unit,
    target: CanonicalName?,
    name: String,
    block: FileSpec.Builder.() -> Unit,
) {
    stage.emissions += EmitNode(
        emission = {
            add(pkg = target.toPackageName(), name = name) {
                block()
            }
        }
    )
}
