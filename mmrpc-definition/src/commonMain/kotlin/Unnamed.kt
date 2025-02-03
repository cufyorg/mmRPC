package org.cufy.mmrpc

import kotlin.reflect.KProperty

fun interface UnnamedBlock<out T> {
    operator fun invoke(ns: CanonicalName, name: String?): T
}

class Unnamed<out T>(private val block: UnnamedBlock<T>) {
    constructor(block: (CanonicalName) -> T) : this({ ns, _ -> block(ns) })
    constructor(value: T) : this({ _, _ -> value })

    fun get(namespace: CanonicalName) =
        block(namespace, name = null)

    fun get(namespace: CanonicalName, name: String) =
        block(namespace, name)

    fun get(obj: NamespaceObject) =
        block(obj.namespace, name = null)

    fun get(obj: NamespaceObject, name: String) =
        block(obj.namespace, name)

    private val values = mutableMapOf<Pair<CanonicalName, String?>, T>()

    operator fun getValue(namespace: CanonicalName, property: KProperty<*>): T {
        return values.getOrPut(namespace to property.name) {
            val splits = property.name.split("__")
            val ns = namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun getValue(obj: NamespaceObject, property: KProperty<*>): T {
        return values.getOrPut(obj.namespace to property.name) {
            val splits = property.name.split("__")
            val ns = obj.namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Unnamed<T> {
        return Unnamed { namespace, name -> block(namespace, name) }
    }
}
