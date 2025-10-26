package commands

import protocol.RespEncoder
import storage.InMemoryStore

class TypeCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.isEmpty()) {
            return encoder.encodeError("pass the key name")
        }

        val key = args[0]
        val type = store.type(key)

        return encoder.encodeSimpleString(type)
    }
}
