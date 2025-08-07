package com.smartlawyer.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val USER_EMAIL = "user_email"
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit { putBoolean(IS_LOGGED_IN, loggedIn) }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(IS_LOGGED_IN, false)

    fun saveLoginSession(email: String) {
        prefs.edit {
            putBoolean(IS_LOGGED_IN, true)
            putString(USER_EMAIL, email)
        }
    }

    fun getSavedEmail(): String = prefs.getString(USER_EMAIL, "") ?: ""

    fun clearSession() {
        prefs.edit { clear() }
    }
}