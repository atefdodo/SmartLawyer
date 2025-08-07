package com.smartlawyer.ui.viewmodels

import android.app.Application
import android.util.Patterns
import androidx.biometric.BiometricManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartlawyer.ui.utils.SessionManager
import com.smartlawyer.ui.utils.StringResources
import com.smartlawyer.ui.utils.UserPreferences
import com.smartlawyer.ui.utils.getStringByKey
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application.applicationContext)
    private val userPreferences = UserPreferences(application.applicationContext)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage



    init {
        checkLoginStatus()
    }

    fun login(
        email: String?, password: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val context = getApplication<Application>()

            when {
                email.isNullOrBlank() || password.isBlank() -> {
                    val msg = context.getStringByKey(StringResources.ERROR_INVALID_CREDENTIALS)
                    _errorMessage.value = msg
                    onFailure(msg)
                }
                !isValidEmail(email) -> {
                    val msg = context.getStringByKey(StringResources.ERROR_INVALID_EMAIL_FORMAT)
                    _errorMessage.value = msg
                    onFailure(msg)
                }
                else -> {
                    val savedCredentials = UserPreferences.getCredentials(context)
                    if (email == savedCredentials.first && password == savedCredentials.second) {
                        sessionManager.saveLoginSession(email)
                        userPreferences.setLoggedIn(true)
                        _isLoggedIn.value = true
                        _userEmail.value = email
                        onSuccess()
                    } else {
                        val msg = context.getStringByKey(StringResources.ERROR_INVALID_CREDENTIALS)
                        _errorMessage.value = msg
                        onFailure(msg)
                    }
                }
            }

            _isLoading.value = false
        }
    }

    fun register(
        email: String, password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val context = getApplication<Application>()

            when {
                email.isBlank() -> {
                    val msg = context.getStringByKey(StringResources.ERROR_EMAIL_REQUIRED)
                    _errorMessage.value = msg
                    onFailure(msg)
                }

                !isValidEmail(email) -> {
                    val msg = context.getStringByKey(StringResources.ERROR_INVALID_EMAIL_FORMAT)
                    _errorMessage.value = msg
                    onFailure(msg)
                }

                password.isBlank() -> {
                    val msg = context.getStringByKey(StringResources.ERROR_PASSWORD_REQUIRED)
                    _errorMessage.value = msg
                    onFailure(msg)
                }

                password.length < 6 -> {
                    val msg = context.getStringByKey(StringResources.ERROR_WEAK_PASSWORD)
                    _errorMessage.value = msg
                    onFailure(msg)
                }

                email == UserPreferences.getCredentials(context).first -> {
                    val msg = context.getStringByKey(StringResources.ERROR_EMAIL_ALREADY_REGISTERED)
                    _errorMessage.value = msg
                    onFailure(msg)
                }

                else -> {
                    try {
                        UserPreferences.saveCredentials(context, email, password)
                        sessionManager.saveLoginSession(email)
                        userPreferences.setLoggedIn(true)
                        _isLoggedIn.value = true
                        _userEmail.value = email
                        onSuccess()
                    } catch (_: Exception) {
                        val msg = context.getStringByKey(StringResources.ERROR_OPERATION_FAILED)
                        _errorMessage.value = msg
                        onFailure(msg)
                    }
                }
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                sessionManager.clearSession()
                userPreferences.setLoggedIn(false)
                _isLoggedIn.value = false
                _userEmail.value = ""
                _errorMessage.value = null
            } catch (_: Exception) {
                _errorMessage.value =
                    getApplication<Application>().getStringByKey(StringResources.ERROR_OPERATION_FAILED)
            }
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                val email = sessionManager.getSavedEmail()
                val isLoggedIn = userPreferences.isLoggedIn.first()
                _isLoggedIn.value = email.isNotEmpty() && isLoggedIn
                _userEmail.value = if (_isLoggedIn.value) email else ""
            } catch (_: Exception) {
                _errorMessage.value =
                    getApplication<Application>().getStringByKey(StringResources.ERROR_OPERATION_FAILED)
                _isLoggedIn.value = false
                _userEmail.value = ""
            }
        }
    }

    fun biometricLogin(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val context = getApplication<Application>()

            try {
                val credentials = UserPreferences.getCredentials(context)
                if (credentials.first.isNotEmpty()) {
                    sessionManager.saveLoginSession(credentials.first)
                    userPreferences.setLoggedIn(true)
                    _isLoggedIn.value = true
                    _userEmail.value = credentials.first
                    onSuccess()
                } else {
                    val msg = context.getStringByKey(StringResources.BIOMETRIC_ERROR_NO_SAVED_DATA)
                    _errorMessage.value = msg
                    onFailure(msg)
                }
            } catch (_: Exception) {
                val msg = context.getStringByKey(StringResources.BIOMETRIC_ERROR_GENERAL)
                _errorMessage.value = msg
                onFailure(msg)
            }

            _isLoading.value = false
        }
    }

    fun isBiometricAvailable(): Boolean {
        return try {
            val biometricManager = BiometricManager.from(getApplication())
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                    BiometricManager.BIOMETRIC_SUCCESS
        } catch (_: Exception) {
            false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun isValidEmail(email: String?): Boolean {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}