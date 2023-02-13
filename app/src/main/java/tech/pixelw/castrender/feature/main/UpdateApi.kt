package tech.pixelw.castrender.feature.main

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface UpdateApi {

    companion object {
        private const val BASE_URL = "https://lapi.pixelw.tech/castrender/"
        val INSTANCE: UpdateApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UpdateApi::class.java)
        }
    }

    @GET("app_update.json")
    suspend fun getUpdate(): AppUpdate

}