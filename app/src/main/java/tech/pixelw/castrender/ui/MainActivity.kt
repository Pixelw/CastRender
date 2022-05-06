package tech.pixelw.castrender.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMainBinding
import tech.pixelw.castrender.ui.browser.MediaBrowserActivity
import tech.pixelw.castrender.ui.controller.ControllerActivity
import tech.pixelw.castrender.ui.render.PlayerActivity
import tech.pixelw.castrender.ui.render.RenderManager.renderService
import tech.pixelw.castrender.utils.ImageLoader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.handler = Handler()
        renderService.hello()
        loadBlur()
    }

    inner class Handler {
        fun openOnClick(v: View?) {
            this@MainActivity.startActivity(
                Intent(
                    this@MainActivity, PlayerActivity::class.java
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
    }

    /**
     * coroutine guide:
     * 在Main线程开始协程
     * loadBlurImage使用withContext()切换到IO线程，同时变为suspend方法
     * 返回值，自动切回Main线程
     * 设置UI背景
     */
    fun loadBlur() {
        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = ImageLoader.loadBlurImage(
                "http://p4.music.126.net/RA7-38iWarhO2xmiZ6TAwg165334668007.jpg",
                this@MainActivity
            )
            bitmap?.let {
                binding.ivBlur.setImageBitmap(it)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}