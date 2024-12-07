package com.example.psyche.helpers

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object ContextUtils {
    fun updateLocale(context: Context, locale: Locale): ContextWrapper {
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val newContext = context.createConfigurationContext(config)
        return ContextWrapper(newContext)
    }
}