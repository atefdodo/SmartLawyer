package com.smartlawyer.ui.utils

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GoogleSignInHelper {
    private const val TAG = "GoogleSignInHelper"
    private const val WEB_CLIENT_ID =
        "443660235499-gt05fpaqc1verbcm3abj34r5bi83icen.apps.googleusercontent.com"

    suspend fun signInWithGoogle(
        context: Context,
        onSuccess: (GoogleIdTokenCredential) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val credentialManager = CredentialManager.create(context)


            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = withContext(Dispatchers.IO) {
                credentialManager.getCredential(context, request)
            }

            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                         onSuccess(credential)

         } catch (_: NoCredentialException) {
             onFailure("لم يتم العثور على بيانات الاعتماد")
         } catch (e: GetCredentialException) {
             onFailure("حدث خطأ أثناء تسجيل الدخول: ${e.message}")
             Log.e(TAG, "Google sign-in failed", e)
         } catch (e: Exception) {
             onFailure("خطأ غير متوقع: ${e.message}")
             Log.e(TAG, "Unexpected error", e)
         }
     }

     suspend fun signUpWithGoogle(
         context: Context,
         onSuccess: (GoogleIdTokenCredential) -> Unit,
         onFailure: (String) -> Unit
     ) {
         try {
             val credentialManager = CredentialManager.create(context)


             val googleIdOption = GetGoogleIdOption.Builder()
                 .setFilterByAuthorizedAccounts(false) // لو عايز تعرض كل الحسابات
                 .setServerClientId(WEB_CLIENT_ID)
                 .build()

             val request = GetCredentialRequest.Builder()
                 .addCredentialOption(googleIdOption)
                 .build()

             val result = withContext(Dispatchers.IO) {
                 credentialManager.getCredential(context, request)
             }

             val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
             onSuccess(credential)

         } catch (_: NoCredentialException) {
             onFailure("لم يتم العثور على بيانات الاعتماد")
         } catch (e: GetCredentialException) {
             onFailure("حدث خطأ أثناء التسجيل: ${e.message}")
             Log.e(TAG, "Google sign-up failed", e)
         } catch (e: Exception) {
             onFailure("خطأ غير متوقع: ${e.message}")
             Log.e(TAG, "Unexpected error", e)
         }
     }

     fun signOut(onComplete: () -> Unit) {
         try {
             // For now, we'll skip the credential clearing since it's not properly implemented
             // This will be implemented once we have the proper Google Sign-In working
         } catch (e: Exception) {
             Log.e(TAG, "Error clearing credential state", e)
         }
         onComplete()
     }

     suspend fun isSignedIn(context: Context): Boolean {
         return try {
             val credentialManager = CredentialManager.create(context)
             val googleIdOption = GetGoogleIdOption.Builder()
                 .setServerClientId(WEB_CLIENT_ID)
                 .setFilterByAuthorizedAccounts(true)
                 .build()

             val request = GetCredentialRequest.Builder()
                 .addCredentialOption(googleIdOption)
                 .build()

             withContext(Dispatchers.IO) {
                 credentialManager.getCredential(context, request)
             }
             true
         } catch (_: Exception) {
             false
         }
     }
 }
