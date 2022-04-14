package tech.pixelw.cling_common

import android.util.Log
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.item.Item

class CustomDIDLParser : DIDLParser() {
    companion object {
        const val KEY_NETEASE_MUSIC_ID = "neteasemusicid"
        const val KEY_QQ_MUSIC_ID = "qqmusicid"
        private const val TAG = "CustomDIDLParser"
    }

    private val additionalMetaMap = HashMap<String, String>()

    override fun parse(xml: String?): DIDLContent {
        val string = if (xml?.contains("y.qq.com", ignoreCase = true) == true) {
            xml.replace("qq=\"", "xmlns:qq=\"") // fix qplay malformed XML
        } else {
            xml
        }
        val parse = super.parse(string)
        additionalMetaMap[KEY_NETEASE_MUSIC_ID]?.let {
            parse?.items?.get(0)?.id = "$KEY_NETEASE_MUSIC_ID:$it"
        }
        additionalMetaMap[KEY_QQ_MUSIC_ID]?.let {
            parse?.items?.get(0)?.id = "$KEY_QQ_MUSIC_ID:$it"
        }
        additionalMetaMap.clear()
        return parse
    }

    inner class CustomItemHandler(instance: Item?, parent: Handler<*>?) :
        ItemHandler(instance, parent) {
        override fun endElement(uri: String?, localName: String?, qName: String?) {
            super.endElement(uri, localName, qName)
            when (uri) {
                "http://y.qq.com/qplay/2.0/" -> {
                    Log.d(
                        TAG,
                        "endElement() called with: uri = $uri, localName = $localName, qName = $qName"
                    )
                    if ("songID".equals(localName, ignoreCase = true)) {
                        val value = getCharacters()
                        Log.d(TAG, "got qq_songId = $value")
                        additionalMetaMap[KEY_QQ_MUSIC_ID] = value
                    }
                }

                "http://music.163.com/dlna/" -> {
                    Log.d(
                        TAG,
                        "endElement() called with: uri = $uri, localName = $localName, qName = $qName"
                    )
                    if ("musicId".equals(localName, ignoreCase = true)) {
                        val value = getCharacters()
                        Log.d(TAG, "got netease song id = $value")
                        additionalMetaMap[KEY_NETEASE_MUSIC_ID] = value
                    }
                }
            }
        }
    }

    override fun createItemHandler(instance: Item?, parent: Handler<*>?): ItemHandler {
        return CustomItemHandler(instance, parent)
    }

}