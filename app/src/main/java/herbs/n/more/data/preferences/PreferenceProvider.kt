package net.simplifiedcoding.mvvmsampleapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val KEY_IN_TRO = "key_intro"

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    fun saveIntro(intro: Int) {
        preference.edit().putInt(
            KEY_IN_TRO,
            intro
        ).apply()
    }

    fun getIntro(): Int? {
        return preference.getInt(KEY_IN_TRO, 0)
    }

}