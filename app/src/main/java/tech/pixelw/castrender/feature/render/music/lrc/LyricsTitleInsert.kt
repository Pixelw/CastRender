package tech.pixelw.castrender.feature.render.music.lrc

data class LyricsTitleInsert(
    private val millis: Long,
    private val title: String?,
    private val artist: String?,
    private val album: String?
) : LyricsListModel {
    override fun type() = TYPE
    override fun millis() = millis
    override fun middleLine() = title
    override fun bottomLine() = album
    override fun upperLine() = artist

    companion object {
        const val TYPE = 2
    }
}