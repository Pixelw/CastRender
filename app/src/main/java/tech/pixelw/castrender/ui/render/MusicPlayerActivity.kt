package tech.pixelw.castrender.ui.render

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import tech.pixelw.castrender.R
import tech.pixelw.castrender.api.NeteaseMusicService
import tech.pixelw.castrender.databinding.ActivityMusicPlayerBinding
import tech.pixelw.castrender.utils.LogUtil

class MusicPlayerActivity : AppCompatActivity() {

    private val neteaseService: NeteaseMusicService by lazy {
        Retrofit.Builder().baseUrl(NeteaseMusicService.BASE_URL).build()
            .create(NeteaseMusicService::class.java)
    }
    private lateinit var binding: ActivityMusicPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player)
        neteaseService.getLyrics("1501477656").enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val string = jsonObject.getString("lyric")
                        LogUtil.i(TAG, string)
                    } catch (ex: JSONException) {
                        onFailure(call, ex)
                    }
                } else {
                    onFailure(call, RuntimeException("response invalid"))
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LogUtil.e(TAG, t.localizedMessage)
            }

        })
    }

    companion object {
        private const val TAG = "MusicPlayerActivity"
    }
}