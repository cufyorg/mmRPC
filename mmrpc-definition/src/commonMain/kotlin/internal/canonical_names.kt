package org.cufy.mmrpc.internal

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Unnamed

internal fun <T : Any> Unnamed<T>.asSiblingOf(cn: CanonicalName, suffix: String) =
    get(cn.namespace, name = cn.name + suffix)
