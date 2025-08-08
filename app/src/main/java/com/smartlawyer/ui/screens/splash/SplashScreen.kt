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
import com.smartlawyer.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    splashDurationMillis: Long = 3000L
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    // State to track if navigation has been triggered
    var hasNavigated by remember { mutableStateOf(false) }

    // Ensure splash screen is visible for the full duration
    LaunchedEffect(Unit) {
        try {
            // Always wait for the full splash duration, regardless of login status
            delay(splashDurationMillis)
            
            // Only navigate if we haven't already
            if (!hasNavigated) {
                hasNavigated = true
                
                // Now check login status and navigate accordingly
                when {
                    isLoggedIn -> {
                        // User is already logged in, go to dashboard
                        navController.navigate("dashboard_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    }

                    // Check if we should try biometric authentication
                    shouldTryBiometric(context, viewModel) -> {
                        navigateToBiometricLogin(navController)
                    }

                    else -> {
                        // Go to normal login
                        navigateToLogin(navController)
                    }
                }
            }
        } catch (_: Exception) {
            // On any error, go to login screen
            if (!hasNavigated) {
                hasNavigated = true
                navigateToLogin(navController)
            }
        }
    }

    // Splash UI - This should always be visible
    SplashContent()
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "جاري التحميل...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Optional: Add a loading indicator
            androidx.compose.material3.CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun shouldTryBiometric(context: android.content.Context, viewModel: AuthViewModel): Boolean {
    return try {
        // Check if context is FragmentActivity and biometric is available
        context is FragmentActivity &&
                viewModel.isBiometricAvailable() &&
                BiometricHelper.isBiometricAvailable(context) == BiometricStatus.AVAILABLE
    } catch (_: Exception) {
        false
    }
}

private fun navigateToBiometricLogin(navController: NavController) {
    try {
        navController.navigate("biometric_login_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    } catch (_: Exception) {
        navigateToLogin(navController)
    }
}

private fun navigateToLogin(navController: NavController) {
    try {
        navController.navigate("login_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    } catch (_: Exception) {
        // If navigation fails, at least we tried
    }
}