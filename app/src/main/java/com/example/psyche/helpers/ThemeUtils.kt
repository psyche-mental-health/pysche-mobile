package com.example.psyche.helpers

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode

object ThemeUtils {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    fun isDarkModeEnabled(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_DARK_MODE, isSystemDarkModeEnabled(context))
    }

    fun setDarkModeEnabled(context: Context, enabled: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) MODE_NIGHT_YES else MODE_NIGHT_NO
        )
    }

    private fun isSystemDarkModeEnabled(context: Context): Boolean {
        return when (getDefaultNightMode()) {
            MODE_NIGHT_YES -> true
            MODE_NIGHT_NO -> false
            MODE_NIGHT_FOLLOW_SYSTEM, MODE_NIGHT_UNSPECIFIED -> {
                val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                currentNightMode == Configuration.UI_MODE_NIGHT_YES
            }
            else -> false
        }
    }
}