package org.cufy.mmrpc.experimental

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.cufy.mmrpc.MmrpcSpec

private val FORMAT = Yaml(
    configuration = YamlConfiguration(
        encodeDefaults = false,
        strictMode = false,
    )
)

fun MmrpcSpec.toYamlString(): String {
    return FORMAT.encodeToString(this)
}

fun MmrpcSpec.Companion.fromYamlString(source: String): MmrpcSpec {
    return FORMAT.decodeFromString(source)
}
