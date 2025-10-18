package commands

import protocol.RespEncoder
import storage.InMemoryStore

class SetCommand(
    private val store: InMemoryStore,
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        if (args.size < 2) {
            return encoder.encodeError("wrong number of arguments for 'set' command")
        }

        val key = args[0]
        val value = args[1]

        // parse optional expiry arguments
        val expiryInMillis = parseExpiry(args.drop(2))

        store.set(key, value, expiryInMillis)
        return encoder.encodeSimpleString("OK")
    }

    private fun parseExpiry(options: List<String>): Long? {
        var i = 0
        while (i < options.size - 1) {
            val option = options[i].uppercase()
            val valueStr = options[i + 1]

            when (option) {
                "PX" -> {
                    val milliseconds = valueStr.toLongOrNull()
                    if (milliseconds != null && milliseconds > 0) {
                        return milliseconds
                    }
                }
                "EX" -> {
                    val seconds = valueStr.toLongOrNull()
                    if (seconds != null && seconds > 0) {
                        val milliseconds = seconds * 1000
                        return milliseconds
                    }
                }
            }
            i += 2
        }
        return null
    }
}
