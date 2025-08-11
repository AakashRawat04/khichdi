package server

import java.net.ServerSocket
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

class RedisServer(private val port: Int = 6379) {
    private val commandHandler = CommandHandler()

    fun start() {
        val serverSocket = ServerSocket(port)
        println("Redis server started on port $port")

        while (true) {
            val clientSocket = serverSocket.accept()
            Thread { handleClient(clientSocket) }.start()
        }
    }

    private fun handleClient(clientSocket: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val writer = PrintWriter(clientSocket.getOutputStream(), true)

            val input = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                input.append(line).append("\r\n")
                if (isCompleteCommand(input.toString())) {
                    val response = commandHandler.handleCommand(input.toString())
                    writer.print(response)
                    writer.flush()
                    input.clear()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            clientSocket.close()
        }
    }

    private fun isCompleteCommand(input: String): Boolean {
        val lines = input.split("\r\n").filter { it.isNotEmpty() }
        if (lines.isEmpty() || !lines[0].startsWith("*")) return false

        val expectedArgs = lines[0].substring(1).toIntOrNull() ?: return false
        var actualArgs = 0

        var i = 1
        while (i < lines.size) {
            if (lines[i].startsWith("$")) {
                val length = lines[i].substring(1).toIntOrNull() ?: return false
                if (i + 1 >= lines.size) return false // Missing value line

                val valueLine = lines[i + 1]
                if (valueLine.length != length) return false // Incomplete value

                actualArgs++
                i += 2 // Skip length line and value line
            } else {
                i++
            }
        }

        return actualArgs == expectedArgs
    }
}