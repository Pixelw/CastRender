package tech.pixelw.castrender.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 网易云歌词 API
 * @author Carl Su "Pixelw"
 * @date 2022/3/25
 */
interface NeteaseMusicService {

    companion object{
        const val BASE_URL = "http://music.163.com"
    }

    @GET("api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1")
    fun getLyrics(@Query("id") id:String): Call<ResponseBody>
}