package tech.pixelw.castrender.feature.main

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface UpdateApi {

    companion object {
        private const val BASE_URL = "https://lapi.pixelw.tech/castrender"
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(UpdateApi::class.java)
        }
        const val K_VERSION_CODE = "versionCode"
        const val K_URL = "url"
    }

    @GET("app_update.json")
    suspend fun getUpdate(): String

}