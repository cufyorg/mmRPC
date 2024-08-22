package org.cufy.mmrpc.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.cufy.jose.JWKSet
import org.cufy.jose.sign
import org.cufy.jose.toJWT
import org.cufy.json.serializeToJsonString
import org.cufy.json.set
import org.cufy.mmrpc.KafkaEndpointInfo
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject

suspend inline fun <reified I : StructObject> KafkaProducer<String, String>.emit(
    routine: RoutineObject<I, *>,
    input: I,
) {
    val endpoints = routine.__info__.endpoints
        .filterIsInstance<KafkaEndpointInfo>()

    require(endpoints.isNotEmpty()) {
        "Routine does not have any Kafka endpoints"
    }

    val keyString = generateKey(routine, input)
    val valueString = input.serializeToJsonString()
    val headers = mapOf("Content-Type" to "application/json")

    for (endpoint in endpoints) {
        val record = ProducerRecord(
            /* topic = */  endpoint.topic.value,
            /* partition = */ null,
            /* timestamp = */ null,
            /* key = */ keyString,
            /* value = */ valueString,
            /* headers = */ headers.entries.map {
                RecordHeader(it.key, it.value.encodeToByteArray())
            }
        )

        withContext(Dispatchers.IO) {
            send(record)
        }
    }
}

suspend inline fun <reified I : StructObject> KafkaProducer<String, String>.emit(
    routine: RoutineObject<I, *>,
    input: I,
    jwks: JWKSet,
) {
    val endpoints = routine.__info__.endpoints
        .filterIsInstance<KafkaEndpointInfo>()

    require(endpoints.isNotEmpty()) {
        "Routine does not have any Kafka endpoints"
    }

    val keyString = generateKey(routine, input)
    val valueString = input.serializeToJsonString()
    val headers = mapOf("Content-Type" to "application/jwt")

    for (endpoint in endpoints) {
        val valueCompactJWS = valueString
            .toJWT { set("topic", endpoint.topic.value) }
            .sign(jwks)

        val record = ProducerRecord(
            /* topic = */  endpoint.topic.value,
            /* partition = */ null,
            /* timestamp = */ null,
            /* key = */ keyString,
            /* value = */ valueCompactJWS.value,
            /* headers = */ headers.entries.map {
                RecordHeader(it.key, it.value.encodeToByteArray())
            }
        )

        withContext(Dispatchers.IO) {
            send(record)
        }
    }
}