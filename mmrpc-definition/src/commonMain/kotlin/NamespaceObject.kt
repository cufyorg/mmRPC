package org.cufy.mmrpc

abstract class NamespaceObject {
    var namespace: CanonicalName
        protected set

    constructor() {
        this.namespace = CanonicalName(inferSegment())
    }

    constructor(vararg segments: String) {
        this.namespace = CanonicalName(segments.asList())
    }

    constructor(parent: NamespaceObject) {
        this.namespace = parent.namespace + inferSegment()
    }

    constructor(parent: NamespaceObject, vararg segments: String) {
        this.namespace = parent.namespace + segments.asList()
    }

    private fun inferSegment(): String {
        return this::class.simpleName ?: toString()
    }
}
