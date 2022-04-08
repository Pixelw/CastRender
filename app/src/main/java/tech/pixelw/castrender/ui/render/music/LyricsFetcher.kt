package tech.pixelw.castrender.ui.render.music

import org.json.JSONObject
import tech.pixelw.castrender.api.NeteaseMusicService
import tech.pixelw.castrender.ui.render.music.lrc.LrcParser


object LyricsFetcher {

    private const val TAG = "LyricsFetcher"

    @JvmStatic
    suspend fun fetchNeteaseMusicLyricById(id: String): List<LrcParser.LrcLine>? {
        val response = NeteaseMusicService.INSTANCE.getLyrics(id)
        val jsonObject = JSONObject(response)
        return if (jsonObject.has("lrc")) {
            val lrc = jsonObject.getJSONObject("lrc").getString("lyric")
            if (jsonObject.has("tlyric")) {
                val trans = jsonObject.getJSONObject("tlyric").getString("lyric")
                LrcParser.parse(lrc, trans, true)
            } else {
                LrcParser.parse(lrc, dropAnnotation = true)
            }
        } else {
            null
        }
    }
}