package com.smartlawyer.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.ui.utils.BiometricHelper
import com.smartlawyer.ui.utils.BiometricStatus
import com.smartlawyer.ui.utils.StringResources
import com.smartlawyer.ui.utils.getStringByKey
import com.smartlawyer.ui.viewmodels.AuthViewModel

/**
 * Screen for biometric login with enhanced error handling and user guidance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // UI states
    var biometricStatus by remember { mutableStateOf<BiometricStatus?>(null) }
    var retryCount by remember { mutableIntStateOf(0) }
    var showErrorCard by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessageFromViewModel by viewModel.errorMessage.collectAsState()

    // Check biometric availability on first launch
    LaunchedEffect(Unit) {
        biometricStatus = BiometricHelper.isBiometricAvailable(context)
    }

    // Show error message if any
    LaunchedEffect(errorMessageFromViewModel) {
        errorMessageFromViewModel?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(context.getStringByKey(StringResources.BIOMETRIC_LOGIN_TITLE)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Biometric icon
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "بصمة",
                        modifier = Modifier.size(120.dp),
                        tint = if (showErrorCard) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = context.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_TITLE),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = context.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_SUBTITLE),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Biometric status handling
                    when (biometricStatus) {
                        BiometricStatus.AVAILABLE -> {
                            // Show retry count if applicable
                            if (retryCount > 0) {
                                Text(
                                    text = context.getStringByKey(StringResources.BIOMETRIC_RETRY_COUNT, retryCount, 3),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Authenticate button
                            Button(
                                onClick = {
                                    BiometricHelper.showBiometricPrompt(
                                        activity = context as androidx.fragment.app.FragmentActivity,
                                        title = context.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_TITLE),
                                        subtitle = context.getStringByKey(StringResources.BIOMETRIC_PLACE_FINGER),
                                        onSuccess = {
                                            viewModel.biometricLogin(
                                                onSuccess = {
                                                    navController.navigate("dashboard_screen") {
                                                        popUpTo(0) { inclusive = true }
                                                    }
                                                },
                                                onFailure = { error ->
                                                    errorMessage = error
                                                    showErrorCard = true
                                                    retryCount++
                                                }
                                            )
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorCard = true
                                            retryCount++
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(context.getStringByKey(StringResources.BIOMETRIC_VERIFYING))
                                }
                            }
                        }
                        else -> {
                            // Show error card for unavailable biometric
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "تحذير",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    biometricStatus?.let { status ->
                                        Text(
                                            text = BiometricHelper.getStatusMessage(context, status),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Help text based on status
                                    biometricStatus?.let { status ->
                                        when (status) {
                                            BiometricStatus.NOT_ENROLLED -> {
                                                Text(
                                                    text = context.getStringByKey(StringResources.BIOMETRIC_ADD_FINGERPRINT_HELP),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                            BiometricStatus.SECURITY_UPDATE_REQUIRED -> {
                                                Text(
                                                    text = context.getStringByKey(StringResources.BIOMETRIC_SECURITY_UPDATE_HELP),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                            else -> {
                                                // No additional help text for other statuses
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Error card (if shown)
                if (showErrorCard) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "خطأ",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Retry button (if applicable)
                    if (showErrorCard && retryCount < 3) {
                        Button(
                            onClick = {
                                showErrorCard = false
                                errorMessage = ""
                                // Retry authentication
                                BiometricHelper.showBiometricPrompt(
                                    activity = context as androidx.fragment.app.FragmentActivity,
                                    title = context.getStringByKey(StringResources.BIOMETRIC_AUTHENTICATION_TITLE),
                                    subtitle = context.getStringByKey(StringResources.BIOMETRIC_PLACE_FINGER),
                                    onSuccess = {
                                        viewModel.biometricLogin(
                                            onSuccess = {
                                                navController.navigate("dashboard_screen") {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            },
                                            onFailure = { error ->
                                                errorMessage = error
                                                showErrorCard = true
                                                retryCount++
                                            }
                                        )
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        showErrorCard = true
                                        retryCount++
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(context.getStringByKey(StringResources.RETRY))
                        }
                    }

                    // Normal login button
                    OutlinedButton(
                        onClick = { navController.navigate("login_screen") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(context.getStringByKey(StringResources.NORMAL_LOGIN))
                    }
                }
            }
        }
    }
}
