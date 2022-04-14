package tech.pixelw.castrender.ui.render.music

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.pixelw.castrender.ui.render.music.lrc.LrcParser
import tech.pixelw.castrender.ui.render.music.lrc.LyricsListModel
import tech.pixelw.castrender.ui.render.music.lrc.LyricsTitleInsert
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.cling_common.CustomDIDLParser
import tech.pixelw.cling_common.entity.MediaEntity

class MusicViewModel : ViewModel() {

    val currentLyrics = MutableLiveData<List<LyricsListModel>>(emptyList())
    val mediaEntity = MutableLiveData<MediaEntity>()

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
        if (mediaEntity.value == null) {
            return lrc
        }
        val title = mediaEntity.value!!.title
        val album = mediaEntity.value!!.album
        val artist = mediaEntity.value!!.artist
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