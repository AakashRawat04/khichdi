package storage

class InMemoryStore {
    private val data = mutableMapOf<String, RedisValue>()

    fun set(
        key: String,
        value: String,
        expiryInMillis: Long? = null,
    ) {
        val expiryAt =
            if (expiryInMillis != null) {
                System.currentTimeMillis() + expiryInMillis
            } else {
                null
            }
        data[key] = RedisValue.StringValue(value, expiryAt)
    }

    fun get(key: String): String? {
        val redisValue = getRedisValueIfNotExpired(key) ?: return null

        return (redisValue as? RedisValue.StringValue)?.value
    }

    fun rpush(
        key: String,
        element: String,
        expiryInMillis: Long? = null,
    ): Long? {
        val existingValue = getRedisValueIfNotExpired(key)

        when (existingValue) {
            null -> {
                val expiryAt =
                    if (expiryInMillis != null) {
                        System.currentTimeMillis() + expiryInMillis
                    } else {
                        null
                    }
                val newList = RedisValue.ListValue(mutableListOf(element), expiryAt)
                data[key] = newList
                return 1L
            }

            is RedisValue.ListValue -> {
                existingValue.elements.add(element)
                val newLength = existingValue.elements.size.toLong()
                return newLength
            }

            else -> return null
        }
    }

    fun exists(key: String): Boolean = getRedisValueIfNotExpired(key) != null

    private fun getRedisValueIfNotExpired(key: String): RedisValue? {
        val redisValue = data[key] ?: return null

        val expiryAt = redisValue.expiryAt

        if (expiryAt != null && System.currentTimeMillis() > expiryAt) {
            data.remove(key)
            return null
        }

        return redisValue
    }
}
