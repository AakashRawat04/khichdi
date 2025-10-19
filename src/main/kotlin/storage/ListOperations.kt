package storage

class ListOperations {
    fun rpush(
        existingValue: RedisValue?,
        elements: List<String>,
        expiryInMillis: Long?,
    ): Pair<RedisValue.ListValue, Long>? {
        if (elements.isEmpty()) return null

        when (existingValue) {
            null -> {
                // Create new list
                val expiryAt =
                    if (expiryInMillis != null) {
                        System.currentTimeMillis() + expiryInMillis
                    } else {
                        null
                    }
                val newList = RedisValue.ListValue(elements.toMutableList(), expiryAt)
                val length = elements.size.toLong()
                return Pair(newList, length)
            }

            is RedisValue.ListValue -> {
                // Append to existing list
                existingValue.elements.addAll(elements)
                val newLength = existingValue.elements.size.toLong()
                return Pair(existingValue, newLength)
            }

            else -> {
                return null
            }
        }
    }

    fun lrange(
        redisValue: RedisValue?,
        start: Int,
        stop: Int,
    ): List<String> {
        if (redisValue == null || redisValue !is RedisValue.ListValue) {
            return emptyList()
        }

        val listElements = redisValue.elements
        val listSize = listElements.size

        if (start >= listSize || start > stop) {
            return emptyList()
        }

        // Clamp stop to valid range
        val actualStop = stop.coerceAtMost(listSize - 1)

        // Extract sublist (stop is inclusive, so +1 for subList's exclusive end)
        return listElements.subList(start, actualStop + 1)
    }
}
