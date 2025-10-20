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
        val removedValue = store.lpop(key)

        return if (removedValue != null) {
            encoder.encodeBulkString(removedValue)
        } else {
            encoder.encodeNullBulkString()
        }
    }
}
