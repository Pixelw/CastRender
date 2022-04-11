package tech.pixelw.cling_common.entity

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.fourthline.cling.model.ModelUtil
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.item.AudioItem
import org.fourthline.cling.support.model.item.MusicTrack

/**
 * Object representing media info, for MediaRenderer
 * @author Carl Su "Pixelw"
 * @date 2021/10/11
 */
data class MediaEntity(val mediaUrl: String?) : Parcelable {
    var title: String? = null
    var casterName: String? = null
    var duration: Long = 1000
    var mediaType: Int = 0

    //    music
    var mediaArtUrl: String? = null
    var artist: String? = null
    var album: String? = null
    var id: String? = null

    constructor(parcel: Parcel) : this(parcel.readString()) {
        title = parcel.readString()
        casterName = parcel.readString()
        duration = parcel.readLong()
        mediaType = parcel.readInt()
        mediaArtUrl = parcel.readString()
        artist = parcel.readString()
        album = parcel.readString()
        id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mediaUrl)
        parcel.writeString(title)
        parcel.writeString(casterName)
        parcel.writeLong(duration)
        parcel.writeInt(mediaType)
        parcel.writeString(mediaArtUrl)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaEntity> {
        const val TYPE_VIDEO = 0
        const val TYPE_AUDIO = 1
        private val NULL_VALUES = listOf("unknown", "null")

        fun parseFromDIDL(didlMeta: String): MediaEntity? {
            try {
                val item = DIDLParser().parse(didlMeta).items[0]
                return MediaEntity(item.resources[0].value).apply {
                    title = checkUnknown(item.title)
                    checkUnknown(item.resources[0].duration)?.let {
                        duration = ModelUtil.fromTimeString(it)
                    }
                    id = checkUnknown(item.id)
                    mediaType = if (item is AudioItem || item is MusicTrack) {
                        mediaArtUrl = checkUnknown(
                            item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI::class.java)
                                .toString()
                        )
                        artist = checkUnknown(item.creator)
                        album =
                            checkUnknown(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM::class.java))
                        TYPE_AUDIO
                    } else {
                        TYPE_VIDEO
                    }
                }
            } catch (ex: Exception) {
                Log.e("MediaEntity", "exception during ParseMetadataFromDIDL", ex)
            }
            return null
        }

        private fun checkUnknown(string: String?): String? {
            if (string == null) return null
            NULL_VALUES.forEach {
                if (it.equals(string, true)) {
                    return null
                }
            }
            return string
        }

        override fun createFromParcel(parcel: Parcel): MediaEntity {
            return MediaEntity(parcel)
        }

        override fun newArray(size: Int): Array<MediaEntity?> {
            return arrayOfNulls(size)
        }
    }
}