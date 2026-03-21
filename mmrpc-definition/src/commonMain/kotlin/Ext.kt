package org.cufy.mmrpc

/**
 * This interface defines a mechanism for modifying or converting elements
 * of type `T` into elements of type `U`.
 */
interface Ext<in T : ElementDefinition, out U : ElementDefinition> {
    operator fun invoke(element: T): U

    operator fun invoke(element: Unnamed<T>): Unnamed<U> {
        return Unnamed { ns, name -> this(element.get(ns, name)) }
    }
}

/**
 * Creates an unnamed implementation of `Ext` that maps elements of type `T` to type `U`
 * using the provided transformation block. The mapping process uses a cache to store
 * the results for each element for performance optimization.
 *
 * @param block A transformation function that takes an element of type `T` and returns
 *              an unnamed instance of type `U`.
 * @return An unnamed implementation of `Ext` that maps elements of type `T`
 *         to elements of type `U` based on the provided block.
 */
@Marker2
fun <T : ElementDefinition, U : ElementDefinition> ext(block: (T) -> Unnamed<U>): Unnamed<Ext<T, U>> {
    return Unnamed { cn, name ->
        check(name != null) { "Name must be provided to resolve Unnamed<Ext>" }
        val cnSuffix = cn?.segments().orEmpty()
        val cache = mutableMapOf<T, U>()
        object : Ext<T, U> {
            override fun invoke(element: T): U {
                return cache.getOrPut(element) {
                    block(element).get(element.canonicalName + cnSuffix, "-$name")
                }
            }
        }
    }
}
