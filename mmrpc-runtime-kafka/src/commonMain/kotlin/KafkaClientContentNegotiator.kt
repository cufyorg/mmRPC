package org.cufy.mmrpc.runtime.kafka

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader

interface KafkaClientContentNegotiator {
    suspend fun <Req> buildReq(
        topic: String,
        reqSerial: KSerializer<Req>,
        request: Req,
    ): ProducerRecord<*, *>

    object Default : KafkaClientContentNegotiator {
        override suspend fun <Req> buildReq(
            topic: String,
            reqSerial: KSerializer<Req>,
            request: Req,
        ): ProducerRecord<*, *> {
            val reqStr = Json.encodeToString(reqSerial, request)
            val headers = buildMap {
                put("Content-Type", "application/json")
            }
            return ProducerRecord(
                /* topic = */ topic,
                /* partition = */ null,
                /* timestamp = */ null,
                /* key = */ null,
                /* value = */ reqStr,
                /* headers = */ headers.entries.map {
                    RecordHeader(it.key, it.value.encodeToByteArray())
                }
            )
        }
    }
}
