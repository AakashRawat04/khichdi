package server

import commands.*
import protocol.RespParser
import storage.InMemoryStore
import java.io.PrintWriter

class CommandHandler(
    private val blockedClientsManager: BlockedClientsManager
) {
    private val parser = RespParser()
    private val store = InMemoryStore(blockedClientsManager)
    private val blpopCommand = BlpopCommand(store, blockedClientsManager)

    private val commands = mapOf(
        "PING" to PingCommand(),
        "ECHO" to EchoCommand(),
        "SET" to SetCommand(store),
        "GET" to GetCommand(store),
        "RPUSH" to RpushCommand(store),
        "LRANGE" to LrangeCommand(store),
        "LPUSH" to LpushCommand(store),
        "LLEN" to LlenCommand(store),
        "LPOP" to LpopCommand(store),
        "TYPE" to TypeCommand(store),
    )

    fun handleCommand(input: String, writer: PrintWriter? = null): String {
        val parsedCommand = parser.parse(input)

        if (parsedCommand.isEmpty()) {
            return "+ERROR\r\n"
        }

        val commandName = parsedCommand[0].uppercase()
        val args = parsedCommand.drop(1)
        println("Executing command: $commandName with args $args")

        // Special handling for BLPOP which needs the writer
        if (commandName == "BLPOP") {
            return blpopCommand.execute(args, writer)
        }

        return commands[commandName]?.execute(args) ?: "+ERROR unknown command\r\n"
    }
}
