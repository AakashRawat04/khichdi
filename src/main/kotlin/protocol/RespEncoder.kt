package protocol

class RespEncoder {
    fun encodeSimpleString(value: String): String = "+$value\r\n"

    fun encodeBulkString(value: String): String = "$${value.length}\r\n$value\r\n"
}