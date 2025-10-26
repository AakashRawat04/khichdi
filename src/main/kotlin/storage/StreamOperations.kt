package storage

class StreamOperations {
    fun xadd(
        existingValue: RedisValue?,
        id: String,
        fields: MutableMap<String, String>,
        expiryInMillis: Long?
    ): RedisValue.StreamValue {
        when (existingValue) {
            null -> {
                val expiryAt =
                    if (expiryInMillis != null) {
                        System.currentTimeMillis() + expiryInMillis
                    } else {
                        null
                    }
                return RedisValue.StreamValue(
                    entries = mutableListOf(
                        StreamEntry(
                            id = id,
                            fields = fields,
                        )
                    ),
                    expiryAt = expiryAt
                )
            }

            is RedisValue.StreamValue -> {
                existingValue.entries.addLast(
                    StreamEntry(
                        id = id,
                        fields = fields,
                    )
                )
                return existingValue
            }

            else -> {
                throw IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value")
            }
        }
    }
}
