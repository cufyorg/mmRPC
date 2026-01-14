package org.cufy.mmrpc

import kotlin.reflect.KProperty

interface Unnamed<out T : Any> {
    fun get(ns: CanonicalName?, name: String?): T
}

fun <T : Any> Unnamed(value: T): Unnamed<T> {
    return object : Unnamed<T> {
        override fun get(ns: CanonicalName?, name: String?): T {
            return value
        }
    }
}

fun <T : Any> Unnamed(block: (ns: CanonicalName?, name: String?) -> T): Unnamed<T> {
    val cache = mutableMapOf<Pair<CanonicalName?, String?>, T>()
    return object : Unnamed<T> {
        override fun get(ns: CanonicalName?, name: String?): T {
            return cache.getOrPut(ns to name) {
                block(ns, name)
            }
        }
    }
}

fun <T : Any> Unnamed<T>.get(obj: NamespaceObject, name: String?) = get(obj.canonicalName, name)

operator fun <T : Any> Unnamed<T>.getValue(obj: NamespaceObject, property: KProperty<*>): T {
    val splits = property.name.split("__")
    val ns = obj.canonicalName + splits.dropLast(1)
    val n = splits.last()
    return get(ns, n)
}

operator fun <T : Any> Unnamed<T>.getValue(obj: Any?, property: KProperty<*>): T {
    val splits = property.name.split("__")
    val ns = CanonicalName(splits.dropLast(1))
    val n = splits.last()
    return get(ns, n)
}

operator fun <T : Any> List<Unnamed<T>>.getValue(obj: NamespaceObject, property: KProperty<*>): List<T> {
    val splits = property.name.split("__")
    val ns = obj.canonicalName + splits
    return map { it.get(ns, name = null) }
}

operator fun <T : Any> List<Unnamed<T>>.getValue(obj: Any?, property: KProperty<*>): List<T> {
    val splits = property.name.split("__")
    val ns = CanonicalName(splits)
    return map { it.get(ns, name = null) }
}
