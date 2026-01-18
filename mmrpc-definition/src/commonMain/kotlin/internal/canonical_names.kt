package org.cufy.mmrpc.internal

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Unnamed

internal fun <T : Any> Unnamed<T>.asAnonSiblingOf(cn: CanonicalName, role: String) =
    get(cn.namespace, name = "-${cn.name}_${role}")

internal fun <T : Any> Unnamed<T>.asAnonSiblingOf(cn: CanonicalName, role: String, i: Int) =
    get(cn.namespace, name = "-${cn.name}_${role}${i}")

internal fun <T : Any> Unnamed<T>.asAnonChildOf(cn: CanonicalName, role: String) =
    get(cn, name = "-${role}")

internal fun <T : Any> Unnamed<T>.asAnonChildOf(cn: CanonicalName, role: String, i: Int) =
    get(cn, name = "-${role}${i}")
