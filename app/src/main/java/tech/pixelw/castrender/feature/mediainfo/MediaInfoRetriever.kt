package tech.pixelw.castrender.feature.mediainfo

import android.text.TextUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class MediaInfoRetriever(player: ExoPlayer) : Player.Listener {
    var mediaInfo = MediaInfo()

    /**
     * 要在载入媒体前初始化
     *
     * @param player Exoplayer
     */
    init {
        player.addListener(this)
    }

    override fun onTracksChanged(tracks: Tracks) {
        var count = 0
        tracks.groups.forEach { group ->
            for (f in 0 until group.length) {
                val format = group.getTrackFormat(f)
                if (format.sampleMimeType?.isNotBlank() == true) {
                    count++
                    if (format.sampleMimeType!!.startsWith("audio")) {
                        val audio = MediaInfo.Audio(count, format.codecs, format.channelCount, format.sampleRate)
                        mediaInfo.tracks.add(audio)
                    } else if (format.sampleMimeType!!.startsWith("video")) {
                        val video = MediaInfo.Video(
                            count,
                            format.width,
                            format.height,
                            format.frameRate,
                            format.codecs,
                            format.pixelWidthHeightRatio
                        )
                        mediaInfo.tracks.add(video)
                    }
                }
            }
        }
    }

    @Deprecated("old interface")
    fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray?) {
        var count = 0
        for (i in 0 until trackGroups.length) {
            val trackGroup = trackGroups[i]
            for (f in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(f)
                if (!TextUtils.isEmpty(format.sampleMimeType)) {
                    count++
                    if (format.sampleMimeType!!.startsWith("audio")) {
                        val audio = MediaInfo.Audio(count, format.codecs, format.channelCount, format.sampleRate)
                        mediaInfo.tracks.add(audio)
                    } else if (format.sampleMimeType!!.startsWith("video")) {
                        val video = MediaInfo.Video(
                            count,
                            format.width,
                            format.height,
                            format.frameRate,
                            format.codecs,
                            format.pixelWidthHeightRatio
                        )
                        mediaInfo.tracks.add(video)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MediaInfoRetriever"
    }
}