package com.smartlawyer.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "user_preferences"

// Context extension property for DataStore
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {

    companion object {
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_LANGUAGE = stringPreferencesKey("language")

        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")


        /** Static helper: Returns Pair(email, password) **/
        suspend fun getCredentials(context: Context): Pair<String, String> {
            val preferences = context.dataStore.data.first()
            val username = preferences[KEY_USERNAME] ?: ""
            val password = preferences[KEY_PASSWORD] ?: ""
            return Pair(username, password)
        }

        /** Static helper: Save email and password **/
        suspend fun saveCredentials(context: Context, username: String, password: String) {
            context.dataStore.edit { prefs ->
                prefs[KEY_USERNAME] = username
                prefs[KEY_PASSWORD] = password
            }
        }
    }

    /** Reactive getters (as Flow) **/
    val username: Flow<String> = context.dataStore.data
        .map { it[KEY_USERNAME] ?: "" }

    val password: Flow<String> = context.dataStore.data
        .map { it[KEY_PASSWORD] ?: "" }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_USE_BIOMETRIC] ?: false }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_IS_LOGGED_IN] ?: false }

    val language: Flow<String> = context.dataStore.data
        .map { it[KEY_LANGUAGE] ?: "ar" }

    val isRememberMe: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_REMEMBER_ME] ?: false }

    suspend fun setRememberMe(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REMEMBER_ME] = enabled }
    }


    /** Setters **/
    suspend fun setUsername(value: String) {
        context.dataStore.edit { it[KEY_USERNAME] = value }
    }

    suspend fun setPassword(value: String) {
        context.dataStore.edit { it[KEY_PASSWORD] = value }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_USE_BIOMETRIC] = enabled }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[KEY_IS_LOGGED_IN] = loggedIn }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    /** Clear all stored preferences **/
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
