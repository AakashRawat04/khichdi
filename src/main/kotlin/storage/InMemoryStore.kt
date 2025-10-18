package storage

data class StoredValue(
    val value: String,
    val expiryAt: Long? = null,
)

class InMemoryStore {
    private val data = mutableMapOf<String, StoredValue>()

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
        data[key] = StoredValue(value, expiryAt)
    }

    fun get(key: String): String? {
        val storedValue = data[key] ?: return null

        if (storedValue.expiryAt != null && System.currentTimeMillis() > storedValue.expiryAt) {
            data.remove(key)
            return null
        }

        return storedValue.value
    }

    fun exists(key: String): Boolean = get(key) != null
}
