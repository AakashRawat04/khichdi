package storage

sealed class RedisValue {
    abstract val expiryAt: Long?

    data class StringValue(
        val value: String,
        override val expiryAt: Long? = null,
    ) : RedisValue()

    data class ListValue(
        val elements: MutableList<String>,
        override val expiryAt: Long? = null,
    ) : RedisValue()
}
