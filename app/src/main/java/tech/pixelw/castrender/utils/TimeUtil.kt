package tech.pixelw.castrender.utils

import java.util.*
import kotlin.math.pow

object TimeUtil {
    @JvmStatic
    fun toTimeString(millis: Long): String {
        sb.setLength(0)
        var timeMs = millis
        val prefix = if (timeMs < 0) "-" else ""
        timeMs = Math.abs(timeMs)
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) formatter.format(
            "%s%d:%02d:%02d",
            prefix,
            hours,
            minutes,
            seconds
        )
            .toString() else formatter.format("%s%02d:%02d", prefix, minutes, seconds)
            .toString()
    }

    var sb = StringBuilder()
    var formatter = Formatter(sb, Locale.getDefault())

    private val factorDict = intArrayOf(1, 1000, 60000, 3600000)

    /***
     * parse 12:34:56.789
     * Highest unit is hour
     * millis digits >= 1
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun timeStringToMillis(timeStr: String): Long {
        var hasMillis = false
        var millis = 0L
        if (timeStr.contains('.')) {
            require(timeStr.filter { it == '.' }.count() == 1) { "Can't parse more than on dot." }
            hasMillis = true
        }
        val split = timeStr.split(":", ".").toTypedArray()
        require(split.isNotEmpty() && if (hasMillis) split.size <= 4 else split.size <= 3) {
            "Can't parse time string: $timeStr"
        }
        split.forEachIndexed { index, s ->
            val factorIndex = split.size - if (hasMillis) index + 1 else index
            if (factorIndex != 0 || s.length == 3) {
                millis += s.toLong() * (factorDict[factorIndex])
            } else if (s.length == 2) {
                millis += s.toLong() * 10
            } else if (s.length == 1) {
                millis += s.toLong() * 100
            } else if (s.length > 3) {
                millis += s.toLong() / 10.0.pow((s.length - 3).toDouble()).toLong()
            }
        }
        return millis
    }


}