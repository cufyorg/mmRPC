package org.cufy.mmrpc.server

import kotlinx.serialization.json.Json

@PublishedApi
internal val json = Json { isLenient = true; ignoreUnknownKeys = true }
