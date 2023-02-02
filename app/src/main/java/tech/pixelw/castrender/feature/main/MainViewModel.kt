package tech.pixelw.castrender.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    fun checkUpdate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val update = UpdateApi.INSTANCE.getUpdate()
//                val jsonObject = JSONObject(update)
//                val ver = jsonObject.getInt(UpdateApi.K_VERSION_CODE)
//                if (ver > BuildConfig.VERSION_CODE) {
//
//                }
            }
        }
    }
}