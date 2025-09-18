package commands

import protocol.RespEncoder
import storage.InMemoryStore

class GetCommand(private val store: InMemoryStore) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.isEmpty()) {
            return encoder.encodeError("wrong number of arguments for 'get' command")
        }

        val key = args[0]
        val value = store.get(key)

        return if (value != null) {
            encoder.encodeBulkString(value)
        } else {
            encoder.encodeNullBulkString()
        }
    }
}
