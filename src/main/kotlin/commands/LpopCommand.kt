package commands

import protocol.RespEncoder
import storage.InMemoryStore

class LpopCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.isEmpty()) {
            return encoder.encodeError("pass the key name")
        }

        val key = args[0]
        val count =
            if (args.size >= 2) {
                val parsedCount = args[1].toIntOrNull()
                if (parsedCount == null || parsedCount < 1) {
                    return encoder.encodeError("count value is not an integer or out of range")
                }
                parsedCount
            } else {
                null
            }
        val pooppedElements =
            if (count != null) {
                store.lpop(key, count)
            } else {
                store.lpop(key, 1)
            }

        return if (count != null) {
            encoder.encodeArray(pooppedElements)
        } else {
            if (pooppedElements.isEmpty()) {
                encoder.encodeNullBulkString()
            } else {
                encoder.encodeBulkString(pooppedElements[0])
            }
        }
    }
}
