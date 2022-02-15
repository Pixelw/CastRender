package tech.pixelw.castrender.utils

import android.text.format.Formatter
import java.lang.StringBuilder
import java.util.*

class TimeUtil {
    companion object instance {
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
    }

}