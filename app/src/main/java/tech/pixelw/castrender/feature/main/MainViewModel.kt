package tech.pixelw.castrender.feature.main

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class MainViewModel : ViewModel() {

    fun checkUpdate(activity: Activity) {
        UpdateHelper.check(viewModelScope, activity)
    }

}