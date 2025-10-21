package server

import protocol.RespEncoder
import java.io.PrintWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Manages clients that are blocked waiting for data on specific keys.
 * Ensures FIFO ordering - first client to block gets served first.
 */
class BlockedClientsManager {
    private val encoder = RespEncoder()
    private val timeoutScheduler = Executors.newScheduledThreadPool(4)

    // Map of key -> queue of blocked clients (FIFO order)
    private val blockedClients = ConcurrentHashMap<String, ConcurrentLinkedQueue<BlockedClient>>()

    data class BlockedClient(
        val key: String,
        val timestamp: Long,
        val writer: PrintWriter,
        val timeout: Long,
        var isTimedOut: Boolean = false
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

        // Schedule timeout if not infinite
        if (timeout != Long.MAX_VALUE) {
            timeoutScheduler.schedule({
                handleTimeout(key, client)
            }, timeout, TimeUnit.MILLISECONDS)
        }
    }

    /**
     * Handle timeout for a blocked client
     */
    private fun handleTimeout(key: String, client: BlockedClient) {
        val queue = blockedClients[key] ?: return

        // Remove the client from the queue
        if (queue.remove(client)) {
            // Mark as timed out and send null array response
            client.isTimedOut = true
            try {
                val response = encoder.encodeNullArray()
                client.writer.print(response)
                client.writer.flush()
                println("Client timed out on key: $key")
            } catch (e: Exception) {
                println("Failed to send timeout response: ${e.message}")
            }

            // Clean up empty queues
            if (queue.isEmpty()) {
                blockedClients.remove(key)
            }
        }
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

        // Only send response if client hasn't timed out
        if (client.isTimedOut) {
            return false
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
