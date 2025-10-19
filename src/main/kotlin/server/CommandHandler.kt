package server

import commands.*
import protocol.RespParser
import storage.InMemoryStore

class CommandHandler {
    private val parser = RespParser()
    private val store = InMemoryStore()
    private val commands = mapOf(
        "PING" to PingCommand(),
        "ECHO" to EchoCommand(),
        "SET" to SetCommand(store),
        "GET" to GetCommand(store),
        "RPUSH" to RpushCommand(store)
    )

    fun handleCommand(input: String): String {
        val parsedCommand = parser.parse(input)

        if (parsedCommand.isEmpty()) {
            return "+ERROR\r\n"
        }

        val commandName = parsedCommand[0].uppercase()
        val args = parsedCommand.drop(1)
        println("Executing command: $commandName with args $args")

        return commands[commandName]?.execute(args) ?: "+ERROR unknown command\r\n"
    }
}
