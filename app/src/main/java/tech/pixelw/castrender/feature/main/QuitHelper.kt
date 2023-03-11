package tech.pixelw.castrender.feature.main

import kotlinx.coroutines.*
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.cling_common.UpnpServiceManager
import kotlin.system.exitProcess

object QuitHelper {

    @OptIn(DelicateCoroutinesApi::class)
    fun quit() {
        GlobalScope.launch(Dispatchers.Main) {
            UpnpServiceManager.stopUpnpService(CastRenderApp.appContext)
            delay(5000)
            exitProcess(0)
        }


    }
}