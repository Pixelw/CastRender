package tech.pixelw.castrender.api

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 网易云歌词 API
 * @author Carl Su "Pixelw"
 * @date 2022/3/25
 */
interface NeteaseMusicService {

    companion object {
        private const val BASE_URL = "http://music.163.com"
        val INSTANCE: NeteaseMusicService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(NeteaseMusicService::class.java)
        }
    }

    @GET("api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1")
    suspend fun getLyrics(@Query("id") id: String): String

}