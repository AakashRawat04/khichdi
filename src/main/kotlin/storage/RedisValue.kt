package storage

data class StreamEntry(
    val id: String,
    val fields: Map<String, String>,
)

sealed class RedisValue {
    abstract val expiryAt: Long?
    abstract val type: String

    data class StringValue(
        val value: String,
        override val expiryAt: Long? = null,
        override val type: String = "string",
    ) : RedisValue()

    data class ListValue(
        val elements: MutableList<String>,
        override val expiryAt: Long? = null,
        override val type: String = "list",
    ) : RedisValue()

    data class StreamValue(
        val entries: MutableList<StreamEntry>,
        override val expiryAt: Long? = null,
        override val type: String = "stream",
    ) : RedisValue()

}
