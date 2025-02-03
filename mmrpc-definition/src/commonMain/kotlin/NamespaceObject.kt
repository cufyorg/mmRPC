package org.cufy.mmrpc

abstract class NamespaceObject {
    var canonicalName: CanonicalName
        protected set

    constructor() {
        this.canonicalName = CanonicalName(inferSegment())
    }

    constructor(vararg segments: String) {
        this.canonicalName = CanonicalName(segments.asList())
    }

    constructor(parent: NamespaceObject) {
        this.canonicalName = parent.canonicalName + inferSegment()
    }

    constructor(parent: NamespaceObject, vararg segments: String) {
        this.canonicalName = parent.canonicalName + segments.asList()
    }

    private fun inferSegment(): String {
        return this::class.simpleName ?: toString()
    }
}
