package tech.pixelw.castrender.feature.render

import androidx.lifecycle.MutableLiveData

class PlayerViewModel : BasePlayerViewModel() {

    val playPosition = MutableLiveData(0L)

    val progressBarPercent = MutableLiveData(0f)

    val playerType = MutableLiveData("")

    override fun onPositionTick(position: Long) {
        playPosition.postValue(position)
    }

}