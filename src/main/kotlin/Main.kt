import server.RedisServer

fun main(args: Array<String>) {
    val redisServer = RedisServer()
    redisServer.start()
}
