package org.cufy.mmrpc.runtime.kafka.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader

internal suspend inline fun KafkaProducer<*, *>.send(
    topic: String,
    partition: Int?,
    timestamp: Long?,
    key: Any?,
    value: Any?,
    headers: Map<String, ByteArray>,
) {
    val record = ProducerRecord(
        /* topic = */ topic,
        /* partition = */ partition,
        /* timestamp = */ timestamp,
        /* key = */ key,
        /* value = */ value,
        /* headers = */ headers.entries.map {
            RecordHeader(it.key, it.value)
        }
    )

    withContext(Dispatchers.IO) {
        @Suppress("UNCHECKED_CAST")
        this as KafkaProducer<Any?, Any?>
        @Suppress("UNCHECKED_CAST")
        record as ProducerRecord<Any?, Any?>
        this.send(record)
    }
}
