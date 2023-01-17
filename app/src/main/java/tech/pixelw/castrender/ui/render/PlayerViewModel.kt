package tech.pixelw.castrender.ui.render

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.ui.render.player.IPlayer
import tech.pixelw.castrender.ui.render.player.IPlayerCallback
import tech.pixelw.castrender.ui.settings.Pref
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.DLNARendererService

class PlayerViewModel : ViewModel(), IPlayerCallback {

    val media = MutableLiveData<MediaEntity>()

    val playPosition = MutableLiveData(0L)

    val progressBarByMillis = MutableLiveData(0)

    private var binder: DLNARendererService? = null

    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(CastRenderApp.getAppContext())
    }


    fun getPreferencePlayer(): String {
        return sp.getString(Pref.K_PLAYER_SURFACE_TYPE, Pref.V_SURFACE_SURFACE_VIEW)!!
    }

    fun connectToService(player: IPlayer<*>, activityFinish: () -> Unit) {
        binder = RenderManager.renderService
        require(binder != null) { "Unable to connect Render Service" }

        val callback = object : RenderControlImpl.ActivityCallback {
            override fun finishPlayer() {
                activityFinish()
            }

            override fun setMediaEntity(mediaEntity: MediaEntity) {
                media.postValue(mediaEntity)
            }
        }
        val renderControlImpl = RenderControlImpl(player, callback)
        binder!!.registerController(renderControlImpl)
        player.addPlayerCallback(this)
        player.addPlayerCallback(RenderNotifyImpl(binder!!))
    }

    fun disconnect() {
        binder?.notifyTransportStateChanged(TransportState.STOPPED)
        binder?.unregisterController()
    }

    override fun onPositionTick(position: Long) {
        playPosition.postValue(position)
    }

    fun volumeChanged(volumeInt: Int) {

    }

}