package storage

class InMemoryStore {
    private val data = mutableMapOf<String, String>()

    fun set(key: String, value: String) {
        data[key] = value
    }

    fun get(key: String): String? {
        return data[key]
    }

    fun exists(key: String): Boolean {
        return data.containsKey(key)
    }
}
