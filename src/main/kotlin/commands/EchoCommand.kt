package commands

import protocol.RespEncoder

class EchoCommand: Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        return if (args.isNotEmpty()) {
            encoder.encodeBulkString(args[0])
        } else {
            encoder.encodeBulkString("")
        }
    }
}