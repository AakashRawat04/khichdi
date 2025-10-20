package commands

import protocol.RespEncoder
import server.BlockedClientsManager
import storage.InMemoryStore
import java.io.PrintWriter

class BlpopCommand(
    private val store: InMemoryStore,
    private val blockedClientsManager: BlockedClientsManager
) : Command {
    private val encoder = RespEncoder()

    override fun execute(args: List<String>): String {
        return execute(args, null)
    }

    /**
     * Execute BLPOP command with optional writer for blocking behavior.
     * Format: BLPOP key timeout
     *
     * If writer is null, returns error (blocking commands need writer).
     * If list has elements, pops and returns immediately.
     * If list is empty, blocks client until element arrives or timeout.
     */
    fun execute(args: List<String>, writer: PrintWriter?): String {
        if (args.size < 2) {
            return encoder.encodeError("ERR wrong number of arguments for 'blpop' command")
        }

        val key = args[0]
        val timeout = args[1].toDoubleOrNull()

        if (timeout == null || timeout < 0) {
            return encoder.encodeError("ERR timeout is not a float or out of range")
        }

        // Try to pop immediately if element exists
        val element = store.blpop(key)

        if (element != null) {
            // Element available, return immediately
            return encoder.encodeArray(listOf(key, element))
        }

        // List is empty, need to block
        if (writer == null) {
            // Can't block without writer - this shouldn't happen in normal operation
            return encoder.encodeError("ERR cannot block without connection writer")
        }

        // Register client as blocked (timeout is in seconds, convert to millis)
        val timeoutMillis = if (timeout == 0.0) Long.MAX_VALUE else (timeout * 1000).toLong()
        blockedClientsManager.blockClient(key, writer, timeoutMillis)

        // Return empty string to signal "no immediate response"
        // The writer will be used later when data arrives
        return ""
    }
}

