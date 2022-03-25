package tech.pixelw.castrender.entity

import org.fourthline.cling.model.ModelUtil
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.item.AudioItem
import org.fourthline.cling.support.model.item.MusicTrack
import tech.pixelw.castrender.utils.LogUtil

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/11
 */
data class MediaEntity(val mediaUrl: String) {
    var title: String? = null
    var casterName: String? = null
    var duration: Long = 1000
    var mediaType: Int = 0

    //    music
    var mediaArtUrl: String? = null
    var artist: String? = null
    var album: String? = null
    var id: String? = null

    companion object {

        const val TYPE_VIDEO = 0
        const val TYPE_AUDIO = 1

        fun parseFromDIDL(didlMeta: String): MediaEntity? {
            try {
                val item = DIDLParser().parse(didlMeta).items[0]
                return MediaEntity(item.resources[0].value).apply {
                    duration = ModelUtil.fromTimeString(item.resources[0].duration)
                    id = item.id
                    mediaType = if (item is AudioItem || item is MusicTrack) {
                        mediaArtUrl =
                            item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI::class.java)
                                .toString()
                        artist = item.creator
                        album =
                            item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM::class.java)
                        TYPE_AUDIO
                    } else {
                        TYPE_VIDEO
                    }
                }
            } catch (ex: Exception) {
                LogUtil.e("ParseMetadataFromDIDL", "ex", ex)
            }
            return null
        }
    }
}