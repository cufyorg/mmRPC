package org.cufy.mmrpc

import kotlin.reflect.KProperty

interface Unnamed<out T> {
    fun get(ns: CanonicalName?, name: String?): T
}

fun <T> Unnamed(value: T): Unnamed<T> {
    return object : Unnamed<T> {
        override fun get(ns: CanonicalName?, name: String?): T {
            return value
        }
    }
}

fun <T> Unnamed(block: (ns: CanonicalName?, name: String?) -> T): Unnamed<T> {
    val cache = mutableMapOf<Pair<CanonicalName?, String?>, T>()
    return object : Unnamed<T> {
        override fun get(ns: CanonicalName?, name: String?): T {
            return cache.getOrPut(ns to name) {
                block(ns, name)
            }
        }
    }
}

fun <T> Unnamed<T>.get(obj: NamespaceObject, name: String?) = get(obj.canonicalName, name)

operator fun <T> Unnamed<T>.getValue(obj: NamespaceObject, property: KProperty<*>): T {
    val splits = property.name.split("__")
    val ns = obj.canonicalName + splits.dropLast(1)
    val n = splits.last()
    return get(ns, n)
}

operator fun <T> Unnamed<T>.getValue(obj: Any?, property: KProperty<*>): T {
    val splits = property.name.split("__")
    val ns = CanonicalName(splits.dropLast(1))
    val n = splits.last()
    return get(ns, n)
}
