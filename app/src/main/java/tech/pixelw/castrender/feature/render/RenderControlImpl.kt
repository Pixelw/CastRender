package tech.pixelw.castrender.feature.render

import android.os.Handler
import android.os.Looper
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.feature.render.player.IPlayer
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.IDLNARenderControl
import kotlin.math.abs

/**
 * 负责DMR服务->播放器的控制和状态查询
 * All called in workers thread
 *
 * @author Carl Su "Pixelw"
 * @date 2021/12/6
 */
class RenderControlImpl(private val player: IPlayer<*>, private val activityCallback: ActivityCallback) : IDLNARenderControl {

    private val handler = Handler(Looper.getMainLooper())

    override fun type() = TYPE

    override fun prepare(uri: String?, entity: MediaEntity?) {
        handler.post {
            entity?.let { activityCallback.setMediaEntity(it) }
            uri?.let {
                player.prepareMedia(it)
            }
        }
    }

    override fun setRawMetadata(rawMetadata: String?) {
        // ignored
    }

    override fun play() {
        handler.post {
            LogUtil.i(TAG, "play called")
            player.play()
        }
    }

    override fun pause() {
        handler.post {
            LogUtil.i(TAG, "pause called")
            player.pause()
        }
    }

    override fun seek(position: Long) {
        handler.post {
            LogUtil.i(TAG, "seekTo: $position")
            if (position < 3000 && abs(player.position - position) < 3000) {
                LogUtil.w(TAG, "ignored just start seek")
                return@post
            }
            player.seekTo(position)
        }
    }

    override fun stop() {
        handler.post {
            LogUtil.i(TAG, "stop called")
            player.stop()
            activityCallback.finishPlayer()
        }
    }

    override fun getPosition(): Long {
        val position = player.position
        LogUtil.d(TAG, "getPosition=$position")
        return position
    }

    override fun getDuration(): Long {
        val duration = player.duration
        LogUtil.d(TAG, "getDuration=$duration")
        return duration
    }

    override fun getTransportState(): TransportState {
        val state = player.mediaSessionState.toTransportState()
        LogUtil.d(TAG, "getState=" + state.name)
        return state
    }

    override fun getSpeed(): Float {
        return player.speed
    }

    interface ActivityCallback {
        fun finishPlayer()
        fun setMediaEntity(mediaEntity: MediaEntity)
    }

    companion object {

        private const val TAG = "RenderControlImpl"
        const val TYPE = 101
    }

}