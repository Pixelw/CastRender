package tech.pixelw.castrender.feature.render

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.feature.render.player.IPlayer
import tech.pixelw.castrender.feature.render.player.IPlayerCallback
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.DLNARendererService

open class BasePlayerViewModel : ViewModel(), IPlayerCallback {

    val media = MutableLiveData<MediaEntity>()
    protected var service: DLNARendererService? = null
    var isUserDraggingSeekBar = false


    fun connectToService(player: IPlayer<*>, activityFinish: () -> Unit) {
        service = RenderManager.renderService
        require(service != null) { "Unable to connect Render Service" }
        val callback = object : RenderControlImpl.ActivityCallback {
            override fun finishPlayer() {
                activityFinish()
            }

            override fun setMediaEntity(mediaEntity: MediaEntity) {
                media.postValue(mediaEntity)
            }
        }
        service?.registerController(RenderControlImpl(player, callback))
        player.addPlayerCallback(this)
        player.addPlayerCallback(RenderNotifyImpl(service!!))
    }

    fun disconnect() {
        service?.notifyTransportStateChanged(TransportState.STOPPED)
        service?.unregisterController()
    }

    fun volumeChanged(volumeInt: Int) {

    }

    fun onUserDragSlider(value: Float) {
        if (value < 0.0f) {
            isUserDraggingSeekBar = true
        } else {
            isUserDraggingSeekBar = false

        }

    }


}