package tech.pixelw.castrender.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class MainViewModel : ViewModel() {

    fun checkUpdate() {
        UpdateHelper.check(viewModelScope)
    }

}