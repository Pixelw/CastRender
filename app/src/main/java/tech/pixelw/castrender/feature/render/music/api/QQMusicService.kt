package tech.pixelw.castrender.feature.render.music.api

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface QQMusicService {

    companion object {
        private const val BASE_URL = "http://c.y.qq.com"
        val INSTANCE: QQMusicService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create(QQMusicService::class.java)
        }
    }


    @Headers(
        "origin: http://y.qq.com",
        "referer: http://y.qq.com/",
        "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36"
    )
    @GET("lyric/fcgi-bin/fcg_query_lyric_new.fcg")
    suspend fun getLyrics(@Query("musicid") id: String): String
}