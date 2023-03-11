package tech.pixelw.castrender

import android.content.Context
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import tech.pixelw.castrender.utils.report.BuglyReporter
import tech.pixelw.castrender.utils.report.Reporter.solution

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/25
 */
class CastRenderApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        solution = BuglyReporter()
    }

    companion object {
        @JvmStatic
        lateinit var appContext: Context

        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("onLowMemory() called")
    }
}