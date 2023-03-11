package tech.pixelw.castrender.feature.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMainBinding
import tech.pixelw.castrender.feature.browser.MediaBrowserActivity
import tech.pixelw.castrender.feature.controller.ControllerActivity
import tech.pixelw.castrender.feature.render.MediaPlayerTestActivity
import tech.pixelw.castrender.feature.render.RenderManager.renderService
import tech.pixelw.castrender.feature.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.handle = Handler()
        renderService.hello()
        vm.checkUpdate(this)
//        loadBlur()
    }

    inner class Handler {
        fun openOnClick(v: View?) {
            this@MainActivity.startActivity(
                Intent(
                    this@MainActivity, MediaPlayerTestActivity::class.java
                )
            )
        }

        fun mediaBrowserOpen(v: View?) {
            this@MainActivity.startActivity(
                Intent(
                    this@MainActivity,
                    MediaBrowserActivity::class.java
                )
            )
        }

        fun controllerOpen(v: View?) {
            this@MainActivity.startActivity(
                Intent(
                    this@MainActivity,
                    ControllerActivity::class.java
                )
            )
        }

        fun settingsOpen(v: View?) {
            this@MainActivity.startActivity(
                Intent(this@MainActivity, SettingsActivity::class.java)
            )
        }

        fun quitEntirely(view: View?) {
            QuitHelper.quit()
            finish()
        }
    }

    /**
     * coroutine guide:
     * 在Main线程开始协程
     * loadBlurImage使用withContext()切换到IO线程，同时变为suspend方法
     * 返回值，自动切回Main线程
     * 设置UI背景
     */
    fun loadBlur() {
//        lifecycleScope.launch(Dispatchers.Main) {
//            val bitmap = ImageLoader.loadBlurImage(
//                "http://p4.music.126.net/RA7-38iWarhO2xmiZ6TAwg165334668007.jpg",
//                this@MainActivity
//            )
//            bitmap?.let {
//                binding.ivBlur.setImageBitmap(it)
//            }
//        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}