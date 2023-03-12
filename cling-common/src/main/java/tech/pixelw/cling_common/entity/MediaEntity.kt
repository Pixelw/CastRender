package tech.pixelw.cling_common.entity

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.tencent.bugly.crashreport.CrashReport
import org.fourthline.cling.model.ModelUtil
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.item.AudioItem
import tech.pixelw.cling_common.CustomDIDLParser

/**
 * Object representing media info, for MediaRenderer
 * @author Carl Su "Pixelw"
 * @date 2021/10/11
 */
class MediaEntity() : Parcelable {
    var mediaUrl: String? = null
    var title: String? = ""
    var casterName: String? = null
    var duration: Long = 1000
    var mediaType: Int = 0

    //    music
    var mediaArtUrl: String? = null
    var artist: String? = null
    var album: String? = null
    var id: String? = null

    fun durationInt() = duration.toInt()

    constructor(parcel: Parcel) : this() {
        mediaUrl = parcel.readString()
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

    override fun toString(): String {
        return "MediaEntity(mediaUrl=$mediaUrl, title=$title, casterName=$casterName, duration=$duration, mediaType=$mediaType, mediaArtUrl=$mediaArtUrl, artist=$artist, album=$album, id=$id)"
    }

    companion object CREATOR : Parcelable.Creator<MediaEntity> {
        const val TYPE_VIDEO = 0
        const val TYPE_AUDIO = 1
        private val NULL_VALUES = listOf("unknown", "null")

        fun parseFromDIDL(didlMeta: String?): MediaEntity? {
            if (TextUtils.isEmpty(didlMeta)) return null
            try {
                val item = CustomDIDLParser().parse(didlMeta).items?.singleOrNull() ?: return null
                return MediaEntity().apply {
                    mediaUrl = item.resources?.singleOrNull()?.value
                    title = item.title.checkUnknown()
                    item.resources?.singleOrNull()?.duration?.checkUnknown()?.let {
                        duration = ModelUtil.fromTimeString(it)
                    }
                    id = item.id?.checkUnknown()
                    mediaType = if (item is AudioItem) {
                        mediaArtUrl = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI::class.java)?.toString()
                            .checkUnknown()
                        artist = item.creator?.checkUnknown()
                        album = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM::class.java).checkUnknown()
                        TYPE_AUDIO
                    } else {
                        TYPE_VIDEO
                    }
                }
            } catch (ex: Exception) {
                Log.e("MediaEntity", "exception during ParseMetadataFromDIDL", ex)
                CrashReport.postCatchedException(ex)
            }
            return null
        }

        private fun String?.checkUnknown(): String? {
            if (this == null) return null
            NULL_VALUES.forEach {
                if (it.equals(this, true)) {
                    return null
                }
            }
            return this
        }

        override fun createFromParcel(parcel: Parcel): MediaEntity {
            return MediaEntity(parcel)
        }

        override fun newArray(size: Int): Array<MediaEntity?> {
            return arrayOfNulls(size)
        }
    }


}