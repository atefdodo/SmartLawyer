package com.smartlawyer.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.R
import com.smartlawyer.ui.utils.BiometricHelper
import com.smartlawyer.ui.utils.BiometricStatus
import com.smartlawyer.ui.utils.StringResources
import com.smartlawyer.ui.utils.getStringByKey
import com.smartlawyer.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    splashDurationMillis: Long = 2000L
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        delay(splashDurationMillis)

        if (isLoggedIn) {
            // User is already logged in, go to dashboard
            navController.navigate("dashboard_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        } else {
            // Check if biometric is available and user has saved credentials
            val biometricStatus = if (context is FragmentActivity) {
                BiometricHelper.isBiometricAvailable(context)
            } else {
                BiometricStatus.UNAVAILABLE
            }

            if (biometricStatus == BiometricStatus.AVAILABLE &&
                context is FragmentActivity &&
                viewModel.isBiometricAvailable()) {

                // Show biometric prompt
                BiometricHelper.authenticate(
                    context = context,
                    title = context.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_TITLE),
                    subtitle = context.getStringByKey(StringResources.BIOMETRIC_PLACE_FINGER),
                    onSuccess = {
                        viewModel.biometricLogin(
                            onSuccess = {
                                navController.navigate("dashboard_screen") {
                                    popUpTo("splash_screen") { inclusive = true }
                                }
                            },
                            onFailure = { _ ->
                                // Biometric login failed, go to normal login
                                navController.navigate("login_screen") {
                                    popUpTo("splash_screen") { inclusive = true }
                                }
                            }
                        )
                    },
                    onError = { _ ->
                        // Biometric authentication error, go to normal login
                        navController.navigate("login_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    }
                )
            } else {
                // No biometric or not available, go to normal login
                navController.navigate("login_screen") {
                    popUpTo("splash_screen") { inclusive = true }
                }
            }
        }
    }

    // Splash UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mm_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Smart Lawyer",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}