package storage

class InMemoryStore {
    // The single source of truth: unified keyspace for all types
    private val data = mutableMapOf<String, RedisValue>()

    // Delegate type-specific operations to specialized classes
    private val stringOps = StringOperations()
    private val listOps = ListOperations()

    // ===== String Operations =====
    fun set(
        key: String,
        value: String,
        expiryInMillis: Long? = null,
    ) {
        val stringValue = stringOps.createStringValue(value, expiryInMillis)
        data[key] = stringValue
    }

    fun get(key: String): String? {
        val redisValue = getRedisValueIfNotExpired(key)
        return stringOps.getStringValue(redisValue)
    }

    // ===== List Operations =====
    fun rpush(
        key: String,
        elements: List<String>,
        expiryInMillis: Long? = null,
    ): Long? {
        val existingValue = getRedisValueIfNotExpired(key)

        val result = listOps.rpush(existingValue, elements, expiryInMillis)

        if (result == null) {
            return null
        }

        val (updatedList, newLength) = result
        data[key] = updatedList
        return newLength
    }

    fun lpush(
        key: String,
        elements: List<String>,
        expiryInMillis: Long? = null,
    ): Long? {
        val existingValue = getRedisValueIfNotExpired(key)

        val result = listOps.lpush(existingValue, elements, expiryInMillis)

        if (result == null) {
            return null
        }

        val (updatedList, newLength) = result
        data[key] = updatedList
        return newLength
    }

    fun lrange(
        key: String,
        start: Int,
        stop: Int,
    ): List<String> {
        val redisValue = getRedisValueIfNotExpired(key)
        return listOps.lrange(redisValue, start, stop)
    }

    fun llen(key: String): Long {
        val redisValue = getRedisValueIfNotExpired(key)
        return listOps.llen(redisValue)
    }

    fun lpop(
        key: String,
        count: Int = 1,
    ): List<String> {
        val redisValue = getRedisValueIfNotExpired(key)
        val poppedElements = listOps.lpop(redisValue, count)

        if (redisValue is RedisValue.ListValue && redisValue.elements.isEmpty()) {
            data.remove(key)
        }
        return poppedElements
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
