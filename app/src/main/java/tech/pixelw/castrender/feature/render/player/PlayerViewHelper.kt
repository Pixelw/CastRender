package tech.pixelw.castrender.feature.render.player

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.preference.PreferenceManager
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.R
import tech.pixelw.castrender.feature.settings.Pref

object PlayerViewHelper {

    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(CastRenderApp.getAppContext())
    }

    private fun getPreferencePlayer(): String {
        return sp.getString(Pref.K_PLAYER_TYPE, Pref.V_PLAYER_EXO)!!
    }

    private fun getPreferenceSurface(): String {
        return sp.getString(Pref.K_PLAYER_SURFACE_TYPE, Pref.V_SURFACE_SURFACE_VIEW)!!
    }

    @JvmStatic
    fun initPlayerView(context: Context, container: FrameLayout): Pair<String, String> {
        val inflater = LayoutInflater.from(context)
        val preferencePlayer = getPreferencePlayer()
        val preferenceSurface = getPreferenceSurface()
        when (preferencePlayer) {
            Pref.V_PLAYER_EXO -> {
                if (preferenceSurface == Pref.V_SURFACE_SURFACE_VIEW) {
                    inflater.inflate(R.layout.view_exo_player_surface, container, true)
                } else {
                    inflater.inflate(R.layout.view_exo_player_texture, container, true)
                }
            }
            Pref.V_PLAYER_SYS -> {
                if (preferenceSurface == Pref.V_SURFACE_SURFACE_VIEW) {
                    inflater.inflate(R.layout.view_surface_view, container, true)
                } else {
                    inflater.inflate(R.layout.view_texture_view, container, true)
                }
            }
        }
        return Pair(preferencePlayer, preferenceSurface)
    }
}