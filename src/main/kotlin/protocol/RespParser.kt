package protocol

class RespParser {
    fun parse(input: String): List<String>{
        val lines = input.split("\r\n")
        val result = mutableListOf<String>()

        var i = 0
        while (i < lines.size) {
            when {
                lines[i].startsWith("*") -> {
                    val arrayLength = lines[i].substring(1).toInt()
                    i++
                    repeat(arrayLength) {
                        if (i < lines.size && lines[i].startsWith("$")) {
                            i++ // Skip bulk string length
                            if (i < lines.size) {
                                result.add(lines[i])
                                i++
                            }
                        }
                    }
                }
                else -> i++
            }
        }
        return result
    }
}