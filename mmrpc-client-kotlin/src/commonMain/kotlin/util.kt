package org.cufy.mmrpc.client

import org.cufy.hash.sha256
import org.cufy.hash.sha384
import org.cufy.hash.sha512
import org.cufy.text.encodeBase64UrlSafe

/**
 * Calculate the hash of the given [value] for a hash claim in
 * an identity token such as `c_hash` or `at_hash`.
 */
fun calculateHashClaim(algorithm: String, value: String): String {
    // https://openid.net/specs/openid-connect-core-1_0.html#ImplicitIDToken
    // https://openid.net/specs/openid-connect-core-1_0.html#HybridIDToken

    /*
    Access Token hash value.
    Its value is the base64url encoding of the left-most half of the hash of the
    octets of the ASCII representation of the access_token value, where the hash
    algorithm used is the hash algorithm used in the alg Header Parameter of the
    ID Token's JOSE Header.
    For instance, if the alg is RS256, hash the access_token value with SHA-256,
    then take the left-most 128 bits and base64url encode them.
    The at_hash value is a case-sensitive string.
    */

    return when (algorithm) {
        "HS256", "RS256", "ES256", "ES256K", "PS256",
        -> value.sha256().encodeBase64UrlSafe(0, /* 256 / 8 / 2 = */16)

        "HS384", "RS384", "ES384", "PS384",
        -> value.sha384().encodeBase64UrlSafe(0, /* 384 / 8 / 2 = */24)

        "HS512", "RS512", "ES512", "PS512",
        -> value.sha512().encodeBase64UrlSafe(0, /* 512 / 8 / 2 = */32)

        else
        -> error("Cannot produce hash claim for token with algorithm=$algorithm")
    }
}
