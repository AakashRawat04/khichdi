package commands

import protocol.RespEncoder
import storage.InMemoryStore

class SetCommand(private val store: InMemoryStore) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.size < 2) {
            return encoder.encodeError("wrong number of arguments for 'set' command")
        }

        val key = args[0]
        val value = args[1]

        store.set(key, value)
        return encoder.encodeSimpleString("OK")
    }
}
