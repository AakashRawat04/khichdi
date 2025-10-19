package commands

import protocol.RespEncoder
import storage.InMemoryStore

class RpushCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.size < 2) {
            return encoder.encodeError("wrong number of arguments for 'rpush' command")
        }

        val key = args[0]
        val elements = args.drop(1)
        val newLength = store.rpush(key, elements)

        if (newLength == null) {
            return encoder.encodeError("WRONGTYPE Operation against a key holding the wrong kind of value")
        }

        return encoder.encodeInteger(newLength)
    }
}
