package com.smartlawyer.ui.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

/**
 * Utility class for handling biometric authentication
 */
object BiometricHelper {
    private const val TAG = "BiometricHelper"

    /**
     * Check if biometric authentication is available
     */
    fun isBiometricAvailable(context: Context): BiometricStatus {
        return try {
            val biometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
                else -> BiometricStatus.UNAVAILABLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking biometric availability: ${e.message}")
            BiometricStatus.UNAVAILABLE
        }
    }

    /**
     * Authenticate using biometrics - simplified version for splash screen
     */
    fun authenticate(
        context: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        showBiometricPrompt(context, title, subtitle, onSuccess, onError)
    }

    /**
     * Show biometric prompt for authentication
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(activity.getStringByKey(StringResources.CANCEL))
                .build()

            val biometricPrompt = BiometricPrompt(activity, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Biometric authentication succeeded")
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val errorMessage = getBiometricErrorMessage(activity, errorCode, errString.toString())
                    Log.e(TAG, "Biometric authentication error: $errorMessage")
                    onError(errorMessage)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "Biometric authentication failed")
                    onError(activity.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_FAILED))
                }
            })

            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing biometric prompt: ${e.message}")
            onError(activity.getStringByKey(StringResources.BIOMETRIC_ERROR_PROMPT_FAILED, e.message ?: ""))
        }
    }

    /**
     * Get user-friendly error message for biometric errors
     */
    private fun getBiometricErrorMessage(context: Context, errorCode: Int, errString: String): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_NO_HARDWARE)
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_HARDWARE_UNAVAILABLE)
            BiometricPrompt.ERROR_NO_BIOMETRICS -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_NOT_ENROLLED)
            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED)
            BiometricPrompt.ERROR_LOCKOUT -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_LOCKOUT)
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_LOCKOUT_PERMANENT)
            BiometricPrompt.ERROR_CANCELED -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_CANCELLED)
            BiometricPrompt.ERROR_TIMEOUT -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_TIMEOUT)
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_UNABLE_TO_PROCESS)
            BiometricPrompt.ERROR_VENDOR -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_VENDOR)
            else -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_GENERAL, errString)
        }
    }

    /**
     * Get status message for biometric availability
     */
    fun getStatusMessage(context: Context, status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.AVAILABLE -> ""
            BiometricStatus.NO_HARDWARE -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_NO_HARDWARE)
            BiometricStatus.HARDWARE_UNAVAILABLE -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_HARDWARE_UNAVAILABLE)
            BiometricStatus.NOT_ENROLLED -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_NOT_ENROLLED)
            BiometricStatus.SECURITY_UPDATE_REQUIRED -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED)
            BiometricStatus.UNAVAILABLE -> context.getStringByKey(StringResources.BIOMETRIC_ERROR_UNAVAILABLE)
        }
    }
}

/**
 * Enum for biometric status
 */
enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    NOT_ENROLLED,
    SECURITY_UPDATE_REQUIRED,
    UNAVAILABLE
}