package tech.pixelw.castrender.utils.ktx

import androidx.annotation.StringRes
import tech.pixelw.castrender.CastRenderApp

fun getString(@StringRes id: Int) = CastRenderApp.getAppContext().getString(id)
