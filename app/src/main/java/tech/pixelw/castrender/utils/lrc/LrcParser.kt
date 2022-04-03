package tech.pixelw.castrender.utils.lrc

import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.TimeUtil
import java.util.regex.Pattern
import kotlin.math.abs

object LrcParser {

    private const val timeRegex: String = "\\[(\\d{2}:\\d{2}.\\d*)]"
    private val pattern: Pattern by lazy {
        Pattern.compile(timeRegex)
    }

    private var lastTranIndex = 0

    @JvmStatic
    fun parse(lrcString: String, transLrc: String? = null): List<LrcLine> {
        val lrcList = mutableListOf<LrcLine>()
        // TODO: remove 作词曲
        lrcString.lines().forEach { line ->
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                val contentStartIndex = matcher.end()
                matcher.group(1)?.let {
                    val content = line.substring(contentStartIndex, line.length)
                    if (content.isEmpty()) return@let
                    val lrcLine = LrcLine(TimeUtil.timeStringToMillis(it), content)
                    lrcLine.translate = findTranslate(transLrc, lrcLine.millis)
                    LogUtil.d("fdfs", lrcLine.toString())
                    lrcList.add(lrcLine)
                }
            }
        }
        lastTranIndex = 0
        return lrcList
    }

    private fun findTranslate(translate: String?, millis: Long): String? {
        if (translate == null) return null
        val lines = translate.lines()
        for (i in lastTranIndex until lines.size) {
            val line = lines[i]
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                matcher.group(1)?.let {
                    val tTime = TimeUtil.timeStringToMillis(it)
                    if (abs(millis - tTime) < 100) {
                        lastTranIndex = i
                        return line.substring(matcher.end(), line.length)
                    }

                }
            }
        }
        return null
    }


    data class LrcLine(val millis: Long, val content: String) {
        var translate: String? = null
    }
}