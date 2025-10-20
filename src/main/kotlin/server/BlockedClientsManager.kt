package server

import protocol.RespEncoder
import java.io.PrintWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Manages clients that are blocked waiting for data on specific keys.
 * Ensures FIFO ordering - first client to block gets served first.
 */
class BlockedClientsManager {
    private val encoder = RespEncoder()

    // Map of key -> queue of blocked clients (FIFO order)
    private val blockedClients = ConcurrentHashMap<String, ConcurrentLinkedQueue<BlockedClient>>()

    data class BlockedClient(
        val key: String,
        val timestamp: Long,
        val writer: PrintWriter,
        val timeout: Long
    )

    /**
     * Register a client as blocked on a specific key
     */
    fun blockClient(key: String, writer: PrintWriter, timeout: Long) {
        val client = BlockedClient(
            key = key,
            timestamp = System.currentTimeMillis(),
            writer = writer,
            timeout = timeout
        )

        blockedClients.computeIfAbsent(key) { ConcurrentLinkedQueue() }.add(client)
        println("Client blocked on key: $key, total blocked: ${blockedClients[key]?.size}")
    }

    /**
     * Notify the first blocked client (FIFO) that data is available.
     * Returns true if a client was notified, false if no clients were waiting.
     */
    fun notifyBlockedClient(key: String, element: String): Boolean {
        val queue = blockedClients[key] ?: return false
        val client = queue.poll() ?: return false

        // Clean up empty queues
        if (queue.isEmpty()) {
            blockedClients.remove(key)
        }

        // Send response to the blocked client
        // Response format: [key_name, popped_element]
        val response = encoder.encodeArray(listOf(key, element))
        try {
            client.writer.print(response)
            client.writer.flush()
            println("Notified blocked client on key: $key with element: $element")
            return true
        } catch (e: Exception) {
            println("Failed to notify blocked client: ${e.message}")
            return false
        }
    }

    /**
     * Check if any clients are blocked on a specific key
     */
    fun hasBlockedClients(key: String): Boolean {
        return blockedClients[key]?.isNotEmpty() ?: false
    }

    /**
     * Get count of blocked clients for a key (for debugging)
     */
    fun getBlockedClientCount(key: String): Int {
        return blockedClients[key]?.size ?: 0
    }
}

