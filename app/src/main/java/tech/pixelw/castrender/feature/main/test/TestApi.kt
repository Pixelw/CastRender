package tech.pixelw.castrender.feature.main.test

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import tech.pixelw.castrender.utils.SharedOkhttpClient

interface TestApi {

    companion object {
        private const val BASE_URL = "https://lapi.pixelw.tech/"
        val INSTANCE: TestApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(SharedOkhttpClient.client)
                .build()
                .create(TestApi::class.java)
        }
    }

    @GET("/")
    suspend fun getBaidu(): String

}