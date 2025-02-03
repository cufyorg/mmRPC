### 1. Kafka: Event Security: SameClient, SameSoftware, SameService using Signatures

The client, software or service (the entity) is considered authenticated
with itself as the subject when it provides a header `Authorization`
with the `auth-scheme` being the literal `Sig` and the value being
a JWS signed by a trusted key of the entity.

The means for obtaining or verifying the key used for signing
the jwt is out of the scope of this specification.

- `topic` claim:

The jwt should include the claim `topic` which should be the
topic the jwt was dispatched to.

- `iss` claim:

The jwt should include the claim `iss` which should be the
id of the entity.

- `aud` claim:

The jwt should include the claim `aud` which should include
a previously agreed upon name or uri of the recipient. If no
explicit recipient is targeted, this claim should be omitted.

- `v_hash` claim:

The JWT should include the claim `v_hash` with its value being
the base64url encoding of the left-most half of the hash of
the octets of the ASCII representation of the event value,
where the hash algorithm used is the hash algorithm used in
the `alg` Header Parameter of the JWS Header.
This is similar to `c_hash` and `at_hash` at
[https://openid.net/specs/openid-connect-core-1_0.html#HybridIDToken]

Example (ignore new line proceeded with four spaces):

```jws
Authorization: Sig eyJhbGciOiJIUzI1NiIsImtpZCI6Imc3ZlB6MnZMIn0.
    eyJ0b3BpYyI6Ii0tdG9waWMtbmFtZS0tIiwiaXNzIjoiX19zb2Z0d2FyZV9pZF9
    fIiwiYXVkIjoiaHR0cHM6Ly9zZXJ2aWNlLmV4YW1wbGUuY29tLyIsImlhdCI6MT
    UxNjIzOTAyMiwidl9oYXNoIjoiVmJtMmVueFVLb1hiZnRNZm9yaC0tdyJ9.
    Pwr-zMtT1YdmgWw84QOWwCL4nLU8eH1aWTu481uWuM4

{"firstname":"John","lastname":"Doe"}
```
