package tech.pixelw.castrender.feature.render.music

import android.util.Base64
import org.json.JSONObject
import retrofit2.HttpException
import tech.pixelw.castrender.feature.render.music.api.NeteaseMusicService
import tech.pixelw.castrender.feature.render.music.api.QQMusicService
import tech.pixelw.castrender.feature.render.music.lrc.LrcParser


object LyricsFetcher {

    private const val TAG = "LyricsFetcher"

    @Throws(HttpException::class)
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

    @Throws(HttpException::class)
    @JvmStatic
    suspend fun fetchQQMusicLyricsById(id: String): List<LrcParser.LrcLine>? {
        val response = QQMusicService.INSTANCE.getLyrics(id).run {
            substring(18, this.length - 1) // strip MusicJsonCallback()
        }
        val jsonObject = JSONObject(response)
        return if (jsonObject.has("lyric")) {
            val get = jsonObject.getString("lyric")
            val decode = Base64.decode(get, Base64.DEFAULT)
            LrcParser.parse(String(decode), dropAnnotation = true)
        } else {
            null
        }
    }
}