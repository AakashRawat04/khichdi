package commands

import protocol.RespEncoder
import storage.InMemoryStore

class LrangeCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.size < 3) {
            return encoder.encodeError("wrong number of arguments for 'lrange' command")
        }

        val key = args[0]

        val start = args[1].toIntOrNull()
        val stop = args[2].toIntOrNull()

        if (start == null || stop == null) {
            return encoder.encodeError("value is not an integer or out of range")
        }

        if (start < 0 || stop < 0) {
            return encoder.encodeError("negative indexes not yet supported")
        }

        val elements = store.lrange(key, start, stop)

        return encoder.encodeArray(elements)
    }
}
