package tech.pixelw.castrender.utils.report

import android.os.Build
import androidx.preference.PreferenceManager
import com.tencent.bugly.crashreport.CrashReport
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.feature.settings.Pref

class BuglyReporter() : IThirdPartyReport {
    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(CastRenderApp.appContext)
    }

    override fun preInit() {
    }

    override fun init() {
        val strategy = CrashReport.UserStrategy(CastRenderApp.appContext)
        strategy.deviceModel = Build.MODEL
        strategy.deviceID = sp.getString(Pref.K_INSTALL_ID, "")
        CrashReport.initCrashReport(
            CastRenderApp.appContext,
            "9932c92997",
            false
        ) // always disable debug output
    }

    override fun submitException(exception: Throwable) {
        CrashReport.postCatchedException(exception)
    }

    override fun identityUser(id: String) {
        CrashReport.setUserId(id)
    }

    override fun testCrash() {
        CrashReport.testJavaCrash()
    }
}