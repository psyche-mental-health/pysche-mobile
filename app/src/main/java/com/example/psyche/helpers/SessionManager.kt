package com.example.psyche.helpers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val LOGIN_TIME = "login_time"
        private const val SESSION_DURATION = 30 * 24 * 60 * 60 * 1000L
        private const val TOKEN = "token"
        private const val TAG = "SessionManager"
    }

    fun setLogin(isLoggedIn: Boolean, token: String? = null) {
        prefs.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
        if (isLoggedIn) {
            prefs.edit().putLong(LOGIN_TIME, System.currentTimeMillis()).apply()
            token?.let {
                prefs.edit().putString(TOKEN, it).apply()
                Log.d(TAG, "Token generated: $it")
            }
            Log.d(TAG, "User logged in at: ${System.currentTimeMillis()}")
        }
    }

    fun isLoggedIn(): Boolean {
        val loginTime = prefs.getLong(LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val loggedIn = prefs.getBoolean(IS_LOGGED_IN, false) && (currentTime - loginTime) < SESSION_DURATION
        Log.d(TAG, "isLoggedIn: $loggedIn")
        return loggedIn
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }
}