package org.cufy.mmrpc.gen.kotlin.context

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.gen.kotlin.GenException

class FailScope {
    val caught = mutableListOf<GenException>()
}

context(scope: FailScope)
fun catch(element: ElementDefinition, block: () -> Unit) =
    catch(setOf(element.canonicalName), block)

context(scope: FailScope)
fun catch(refs: Iterable<CanonicalName?>, block: () -> Unit) {
    try {
        block()
    } catch (e: GenException) {
        scope.caught += GenException(e.message, e.refs + refs, e)
    }
}

fun rethrow(element: ElementDefinition, block: () -> Unit) =
    rethrow(setOf(element.canonicalName), block)

fun rethrow(refs: Iterable<CanonicalName?>, block: () -> Unit) {
    try {
        block()
    } catch (e: GenException) {
        throw GenException(e.message, e.refs + refs, e)
    }
}

fun fail(element: ElementDefinition, message: String? = null, cause: Throwable? = null): Nothing =
    fail(setOf(element.canonicalName), message, cause)

fun fail(refs: Iterable<CanonicalName?>, message: String? = null, cause: Throwable? = null): Nothing {
    throw GenException(message, refs.toSet(), cause)
}

fun fail(message: String? = null, cause: Throwable? = null): Nothing {
    throw GenException(message, emptySet(), cause)
}
