### 1.1. Http: Security: SameClient using OAuth Bearer Token Usage

The client is considered authenticated with itself
as the subject when it provides an access token
using [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750)
with either its client id in the `client_id` claim
and the `iss` claim is a trusted **subject** identity
provider or its client id in the `sub` claim and
the `iss` is a trusted **client** identity provider.

### 1.2. Http: Security: SameSoftware using OAuth Bearer Token Usage

The client is considered authenticated with its software
as the subject when it provides an access token
using [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750)
with its software id in the `software_id` claim
and the `iss` claim is either a trusted **subject** identity
provider or a trusted **client** identity provider.

### 1.3. Http: Security: SameSubject using OAuth Bearer Token Usage

The client is considered authenticated with some
subject in some issuer when it provides an access token
using [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750)
with the subject matching the `sub` claim and the
subject issuer matching the `iss` claim.
