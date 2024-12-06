package org.cufy.mmrpc

open class FaultException : Exception {
    val canonicalName: CanonicalName

    constructor(canonicalName: CanonicalName) : super() {
        this.canonicalName = canonicalName
    }

    constructor(canonicalName: CanonicalName, message: String?) : super(message) {
        this.canonicalName = canonicalName
    }

    constructor(canonicalName: CanonicalName, message: String?, cause: Throwable?) : super(message, cause) {
        this.canonicalName = canonicalName
    }

    constructor(canonicalName: CanonicalName, cause: Throwable?) : super(cause) {
        this.canonicalName = canonicalName
    }
}
