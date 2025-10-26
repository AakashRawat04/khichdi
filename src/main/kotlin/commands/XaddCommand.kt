package commands

import protocol.RespEncoder
import storage.InMemoryStore

class XaddCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.size < 3) {
            return encoder.encodeError("wrong number of arguments for 'XADD' command")
        }

        val key = args[0]
        val id = args[1]
        val fieldValues = args.subList(2, args.size)

        if (fieldValues.size % 2 != 0) {
            return encoder.encodeError("field-value pairs are not balanced")
        }

        val message = mutableMapOf<String, String>()
        for (i in fieldValues.indices step 2) {
            message[fieldValues[i]] = fieldValues[i + 1]
        }

        val (entryId, errorMessage) = store.xadd(key, id, message)

        if (errorMessage != null) {
            return encoder.encodeError(errorMessage)
        }

        if (entryId == null) {
            return encoder.encodeError("WRONGTYPE Operation against a key holding the wrong kind of value")
        }

        return encoder.encodeBulkString(entryId)
    }
}
