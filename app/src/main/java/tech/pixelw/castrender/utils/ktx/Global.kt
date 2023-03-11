package tech.pixelw.castrender.utils.ktx

import android.widget.Toast
import androidx.annotation.StringRes
import tech.pixelw.castrender.CastRenderApp

fun getString(@StringRes id: Int) = CastRenderApp.appContext.getString(id)


fun toast(text: String) {
    Toast.makeText(CastRenderApp.appContext, text, Toast.LENGTH_LONG).show()
}