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
                existingValue.elements.addAll(elements)
                val newLength = existingValue.elements.size.toLong()
                return Pair(existingValue, newLength)
            }

            else -> {
                return null
            }
        }
    }

    fun lpush(
        existingValue: RedisValue?,
        elements: List<String>,
        expiryInMillis: Long?,
    ): Pair<RedisValue.ListValue, Long>? {
        if (elements.isEmpty()) return null

        when (existingValue) {
            null -> {
                val expiryAt =
                    if (expiryInMillis != null) {
                        System.currentTimeMillis() + expiryInMillis
                    } else {
                        null
                    }
                val reversedElements = elements.reversed()
                val newList = RedisValue.ListValue(reversedElements.toMutableList(), expiryAt)
                val length = reversedElements.size.toLong()
                return Pair(newList, length)
            }

            is RedisValue.ListValue -> {
                existingValue.elements.addAll(0, elements.reversed())
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

        if (listSize == 0) {
            return emptyList()
        }

        val normalizedStart = normalizeIndex(start, listSize)
        val normalizedStop = normalizeIndex(stop, listSize)

        if (normalizedStart >= listSize || normalizedStart > normalizedStop) {
            return emptyList()
        }

        val actualStop = normalizedStop.coerceAtMost(listSize - 1)

        val result = listElements.subList(normalizedStart, actualStop + 1)
        return result
    }

    private fun normalizeIndex(
        index: Int,
        size: Int,
    ): Int =
        when {
            index >= 0 -> index

            index < 0 -> {
                val normalized = size + index
                normalized.coerceAtLeast(0)
            }

            else -> index
        }
}
