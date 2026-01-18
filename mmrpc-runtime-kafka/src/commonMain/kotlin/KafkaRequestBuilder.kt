package org.cufy.mmrpc.runtime.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader

class KafkaRequestBuilder {
    lateinit var topic: String
    var partition: Int? = null
    var timestamp: Long? = null
    var key: Any? = null
    var value: Any? = null
    val headers = mutableMapOf<String, ByteArray>()

    fun build(): ProducerRecord<*, *> {
        return ProducerRecord(
            /* topic = */ topic,
            /* partition = */ partition,
            /* timestamp = */ timestamp,
            /* key = */ key,
            /* value = */ value,
            /* headers = */ headers.entries.map {
                RecordHeader(it.key, it.value)
            }
        )
    }
}
