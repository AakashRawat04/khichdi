package storage

class StringOperations {
    fun createStringValue(
        value: String,
        expiryInMillis: Long?,
    ): RedisValue.StringValue {
        val expiryAt =
            if (expiryInMillis != null) {
                System.currentTimeMillis() + expiryInMillis
            } else {
                null
            }
        return RedisValue.StringValue(value, expiryAt)
    }

    fun getStringValue(redisValue: RedisValue?): String? = (redisValue as? RedisValue.StringValue)?.value
}
