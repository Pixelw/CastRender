package tech.pixelw.castrender.ui.render.music.lrc

interface LyricsListModel {
    fun type(): Int
    fun millis(): Long
    fun middleLine(): CharSequence?
    fun bottomLine(): CharSequence?
    fun upperLine(): CharSequence?

}