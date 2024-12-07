package com.example.psyche.views.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.psyche.helpers.ContextUtils
import com.example.psyche.helpers.ThemeUtils
import java.util.Locale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> get() = _isDarkModeEnabled

    private val _selectedLanguage = MutableLiveData<String>()
    val selectedLanguage: LiveData<String> get() = _selectedLanguage

    init {
        val sharedPreferences =
            application.getSharedPreferences("settings", Application.MODE_PRIVATE)
        _isDarkModeEnabled.value = ThemeUtils.isDarkModeEnabled(application)
        _selectedLanguage.value = sharedPreferences.getString("selected_language", "English")
    }

    fun setDarkModeEnabled(isEnabled: Boolean) {
        _isDarkModeEnabled.value = isEnabled
        ThemeUtils.setDarkModeEnabled(getApplication(), isEnabled)
    }

    fun setSelectedLanguage(language: String) {
        _selectedLanguage.value = language
        val locale = when (language) {
            "Indonesia" -> Locale("in")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = getApplication<Application>().resources.configuration
        config.setLocale(locale)
        getApplication<Application>().resources.updateConfiguration(
            config,
            getApplication<Application>().resources.displayMetrics
        )

        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("settings", Application.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("selected_language", language)
            apply()
        }
    }
}