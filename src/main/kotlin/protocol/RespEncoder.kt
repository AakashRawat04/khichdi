package protocol

class RespEncoder {
    fun encodeSimpleString(value: String): String = "+$value\r\n"

    fun encodeBulkString(value: String): String = "$${value.length}\r\n$value\r\n"

    fun encodeInteger(value: Long): String = ":$value\r\n"

    fun encodeError(message: String): String = "-$message\r\n"

    fun encodeArray(elements: List<String>): String {
        val builder = StringBuilder()
        builder.append("*${elements.size}\r\n")
        elements.forEach { element ->
            builder.append(encodeBulkString(element))
        }
        return builder.toString()
    }

    fun encodeNullBulkString(): String = "$-1\r\n"

    fun encodeNullArray(): String = "*-1\r\n"
}