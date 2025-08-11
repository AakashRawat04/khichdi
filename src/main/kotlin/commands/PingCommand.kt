package commands

import protocol.RespEncoder

class PingCommand: Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        return if (args.isNotEmpty()) {
            encoder.encodeBulkString(args.joinToString(" "))
        } else {
            encoder.encodeSimpleString("PONG")
        }
    }
}