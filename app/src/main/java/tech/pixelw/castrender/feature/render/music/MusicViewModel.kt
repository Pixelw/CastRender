package tech.pixelw.castrender.feature.render.music

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.pixelw.castrender.feature.render.BasePlayerViewModel
import tech.pixelw.castrender.feature.render.music.lrc.LrcParser
import tech.pixelw.castrender.feature.render.music.lrc.LyricsListModel
import tech.pixelw.castrender.feature.render.music.lrc.LyricsTitleInsert
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.cling_common.CustomDIDLParser

class MusicViewModel : BasePlayerViewModel() {

    val currentLyrics = MutableLiveData<List<LyricsListModel>>(emptyList())

    companion object {
        private const val TAG = "MusicViewModel"
    }

    fun fetchLyrics(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val split = id.split(":", limit = 2)
            if (split.size != 2) return@launch
            try {
                var lrc: List<LrcParser.LrcLine>? = null
                when (split[0]) {
                    CustomDIDLParser.KEY_NETEASE_MUSIC_ID -> {
                        lrc = LyricsFetcher.fetchNeteaseMusicLyricById(split[1])
                    }
                    CustomDIDLParser.KEY_QQ_MUSIC_ID -> {
                        lrc = LyricsFetcher.fetchQQMusicLyricsById(split[1])
                    }
                }
                lrc?.let {
                    currentLyrics.postValue(fillLyricsBlank(it))
                }

            } catch (e: Throwable) {
                LogUtil.e(TAG, e.localizedMessage, e)
            }
        }
    }

    private fun fillLyricsBlank(lrc: List<LrcParser.LrcLine>): List<LyricsListModel> {
        if (media.value == null) {
            return lrc
        }
        val title = media.value!!.title
        val album = media.value!!.album
        val artist = media.value!!.artist
        val outList = mutableListOf<LyricsListModel>()
        var lastMillis = 0L
        lrc.forEachIndexed { index, lrcLine ->
            outList.add(lrcLine)
            if (index == 0) {
                if (lrcLine.millis > 3000L) {
                    outList.add(index, LyricsTitleInsert(0L, title, artist, album))
                }
            } else if (index == lrc.size - 1) {
                outList.add(
                    LyricsTitleInsert(
                        lastMillis + (lrcLine.millis() - lastMillis) / 2,
                        title, artist, album
                    )
                )
            } else {
                if (lrcLine.millis - lastMillis > 10000L) {
                    outList.add(
                        outList.size - 1,
                        LyricsTitleInsert(lastMillis + 6000L, title, artist, album)
                    )
                }
            }
            lastMillis = lrcLine.millis()
        }
        return outList
    }
}