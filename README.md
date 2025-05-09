# mmRPC [![](https://jitpack.io/v/org.cufy/mmrpc.svg)](https://jitpack.io/#org.cufy/mmrpc)

Dsl for defining api specifications regardless of the underlying communication technology.
This enables API specification writers to not think about anything but the functionality of
their API and what the "routes" or "functions" actually take as input and return as output.

This project is aimed to help writing specifications for internal project communication and
not for public APIs since it does not give much flexibility for specifying how the actual
communication technology should be configured.

For example, HTTP REST APIs defines the route `path` and `method` and various request body
content types and various response body content types. This makes the API design worry about
things other than what the route actually does. Thus, this specification dsl assumes that
every routine (which is what it is called here), if was declared to have an HTTP Endpoint,
to only support the `POST` method. This way, if the designer wants a function that creates
something and a function that deletes it, the designer is required to define two paths instead
of a single one with two methods. Thus, forcing consistency.

### Ultimate Goal

This is NOT a specification for a new communication protocol but a way to generate
specifications, clients and UI for existing communication protocols.

Someone can use this project to create specification for a http service and not anything
else and the specification still be a valid and compatible http.

Don't be intimidated by the number of modules or generators, these are optional helpers for
the user to make use of the specification they wrote.

### Q/A

- Q - When will you complete the docs?

In a day called "Someday". You are free to remind me when it comes lsafer@gmail.com

- Q - Future Compatibility

Future syntax compatibility is prioritized. Binary compatibility, currently, not at all.

There will be reworks on builders, constructor, constructor-like functions and definition
hierarchy until a suitable and more consistent pattern is achieved.

- Q - Pronunciation and Meaning

Pronunciation: Emu Emu Aru Bee Ci

Meaning: Multi Medium Remote Procedure Call
