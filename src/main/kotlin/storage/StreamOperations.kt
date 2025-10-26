package storage

class StreamOperations {
    fun validateStreamId(entryId: String, lastEntryId: String?=null): String? {
        if (entryId == "*") {
            return null
        }

        val parts = entryId.split("-")
        if (parts.size != 2) {
            return "ERR Invalid stream ID format"
        }

        val timestamp = parts[0].toLongOrNull()
        val sequence = parts[1].toLongOrNull()
        if (timestamp == null || sequence == null || timestamp < 0 || sequence < 0) {
            return "ERR Invalid stream ID format"
        }

        if (timestamp == 0L && sequence == 0L) {
            return "ERR The ID specified in XADD must be greater than 0-0"
        }

        if (lastEntryId != null && lastEntryId != "*") {
            val lastParts = lastEntryId.split("-")
            val lastTimestamp = lastParts[0].toLongOrNull()
            val lastSequence = lastParts[1].toLongOrNull()
            if (lastTimestamp != null && lastSequence != null) {
                if (timestamp < lastTimestamp || (timestamp == lastTimestamp && sequence <= lastSequence)) {
                    return "ERR The ID specified in XADD is equal or smaller than the target stream top item"
                }
            }
        }

        return null
    }

    fun xadd(
        existingValue: RedisValue?,
        id: String,
        fields: MutableMap<String, String>,
        expiryInMillis: Long?
    ): Pair<RedisValue.StreamValue?, String?> {
        when (existingValue) {
            null -> {
                val validationError = validateStreamId(id)
                if (validationError != null) {
                    return Pair(null, validationError)
                }
                val expiryAt =
                    if (expiryInMillis != null) {
                        System.currentTimeMillis() + expiryInMillis
                    } else {
                        null
                    }
                val newStream = RedisValue.StreamValue(mutableListOf(), expiryAt)
                newStream.entries.addLast(
                    StreamEntry(
                        id = id,
                        fields = fields,
                    )
                )
                return Pair(newStream, null)
            }

            is RedisValue.StreamValue -> {
                // Validate ID
                val lastEntryId = existingValue.entries.lastOrNull()?.id
                val validationError = validateStreamId(id, lastEntryId)
                if (validationError != null) {
                    return Pair(null, validationError)
                }
                existingValue.entries.addLast(
                    StreamEntry(
                        id = id,
                        fields = fields,
                    )
                )
                return Pair(existingValue, null)
            }

            else -> {
                return Pair(null, "ERR Operation against a key holding the wrong kind of value")
            }
        }
    }
}
