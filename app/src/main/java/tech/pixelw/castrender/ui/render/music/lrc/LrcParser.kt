package tech.pixelw.castrender.ui.render.music.lrc

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
    private var lastAnnotationIndex = 0
    private val ANNOTATION_KETWORD =
        listOf("作词", "作曲", "编曲", "制作人", "混音", "母带", "管理方", "监制", "版权", "出品", "录音")

    /**
     * 传入lrc字符串进行解析
     */
    @JvmStatic
    fun parse(
        lrcString: String,
        transLrc: String? = null,
        dropAnnotation: Boolean = false
    ): List<LrcLine> {
        val lrcList = mutableListOf<LrcLine>()
        lrcString.lines().forEachIndexed { index, line ->
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                val contentStartIndex = matcher.end()
                matcher.group(1)?.let {
                    val content = line.substring(contentStartIndex, line.length)
                    if (content.isEmpty()) return@let
                    val lrcLine = LrcLine(TimeUtil.timeStringToMillis(it), content.trim())
                    lrcLine.findAnnotation(index)
                    if (lrcLine.isAnnotation) {
                        lastAnnotationIndex = index
                    }
                    if (dropAnnotation && lrcLine.isAnnotation) {
                        return@forEachIndexed   // skip this line
                    }
                    lrcLine.translate = findTranslate(transLrc, lrcLine.millis)
                    LogUtil.d("lrc", lrcLine.toString())
                    lrcList.add(lrcLine)
                }
            }
        }
        lastTranIndex = 0
        lastAnnotationIndex = 0
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
                        return line.substring(matcher.end(), line.length).trim()
                    }

                }
            }
        }
        return null
    }


    data class LrcLine(val millis: Long, val content: String) : LyricsListModel {
        var translate: String? = null
        var isAnnotation = false

        /**
         * 查找是否有作词曲信息
         */
        fun findAnnotation(index: Int) {
            if (index <= lastAnnotationIndex + 1) {
                ANNOTATION_KETWORD.forEach {
                    if (content.contains(it) && (content.contains(":") || content.contains("："))) {
                        isAnnotation = true
                        return
                    }
                }
            }
        }

        override fun type() = TYPE
        override fun millis() = millis
        override fun middleLine() = content
        override fun bottomLine() = translate
        override fun upperLine() = ""
        override fun toString(): String {
            return "LrcLine(millis=$millis, content='$content', translate=$translate, isAnnotation=$isAnnotation)"
        }

        companion object {
            const val TYPE = 1
        }
    }
}