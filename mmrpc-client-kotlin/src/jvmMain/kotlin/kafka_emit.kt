package org.cufy.mmrpc.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.cufy.jose.JWKSet
import org.cufy.jose.JWT
import org.cufy.jose.sign
import org.cufy.json.json
import org.cufy.json.serializeToJsonString
import org.cufy.json.set
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.isKafkaSupported

suspend inline fun <reified I : Any> KafkaProducer<String, String>.emit(
    routine: RoutineObject<I, *>,
    input: I,
    key: String? = null,
) {
    require(routine.comm.isKafkaSupported()) {
        "Routine does not support Kafka communication channel"
    }

    val value = input.serializeToJsonString()
    val headers = mapOf(
        "Content-Type" to "application/json",
    )

    val record = ProducerRecord(
        /* topic = */ routine.canonicalName.value,
        /* partition = */ null,
        /* timestamp = */ null,
        /* key = */ key,
        /* value = */ value,
        /* headers = */ headers.entries.map {
            RecordHeader(it.key, it.value.encodeToByteArray())
        }
    )

    withContext(Dispatchers.IO) {
        send(record)
    }
}

suspend inline fun <reified I : Any> KafkaProducer<String, String>.emit(
    routine: RoutineObject<I, *>,
    input: I,
    key: String? = null,
    iss: String,
    alg: String,
    aud: List<String> = emptyList(),
    jwks: JWKSet,
) {
    require(routine.comm.isKafkaSupported()) {
        "Routine does not support Kafka communication channel"
    }

    val topic = routine.canonicalName.value
    val value = input.serializeToJsonString()
    val token = JWT {
        header["alg"] = alg

        payload["topic"] = topic
        payload["iss"] = iss
        payload["v_hash"] = calculateHashClaim(alg, value)

        if (aud.size == 1) payload["aud"] = aud.first()
        if (aud.size > 1) payload["aud"] = JsonArray(aud.map { it.json })
    }.sign(jwks)

    val headers = mapOf(
        "Content-Type" to "application/json",
        "Authorization" to "Sig ${token.value}",
    )

    val record = ProducerRecord(
        /* topic = */ topic,
        /* partition = */ null,
        /* timestamp = */ null,
        /* key = */ key,
        /* value = */ value,
        /* headers = */ headers.entries.map {
            RecordHeader(it.key, it.value.encodeToByteArray())
        }
    )

    withContext(Dispatchers.IO) {
        send(record)
    }
}
