package tech.pixelw.castrender.utils.lrc

import java.util.regex.Pattern

object LrcParser {

    private const val timeRegex: String = "\\[(\\d{2}:\\d{2}.\\d{2})]"
    private val pattern: Pattern by lazy {
        Pattern.compile(timeRegex, Pattern.MULTILINE)
    }


    @JvmStatic
    fun parse(lrcString: String) {
        // TODO: line split
        val matcher = pattern.matcher(lrcString)
        matcher.group(1)
    }
}