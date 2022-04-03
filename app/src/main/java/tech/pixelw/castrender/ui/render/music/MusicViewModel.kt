package tech.pixelw.castrender.ui.render.music

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.pixelw.castrender.utils.lrc.LrcParser

class MusicViewModel : ViewModel() {

    val currentLyrics = MutableLiveData<List<LrcParser.LrcLine>>(emptyList())

    fun fetchLyrics(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lrc = LyricsFetcher.fetchNeteaseMusicLyricById(id)
            lrc?.let { currentLyrics.postValue(it) }
        }
    }
}