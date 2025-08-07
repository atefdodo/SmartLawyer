package com.smartlawyer.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.ui.utils.StringResources
import com.smartlawyer.ui.utils.getStringByKey
import com.smartlawyer.ui.viewmodels.AuthViewModel

/**
 * Screen for user login with enhanced validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Validation states
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Show error message if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        // Email validation
        if (email.isBlank()) {
            errors["email"] = context.getStringByKey(StringResources.ERROR_EMAIL_REQUIRED)
        } else if (!viewModel.isValidEmail(email)) {
            errors["email"] = context.getStringByKey(StringResources.ERROR_INVALID_EMAIL_FORMAT)
        }

        // Password validation
        if (password.isBlank()) {
            errors["password"] = context.getStringByKey(StringResources.ERROR_PASSWORD_REQUIRED)
        }

        validationErrors = errors
        return errors.isEmpty()
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(context.getStringByKey(StringResources.LOGIN_TITLE)) },
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
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        validationErrors = validationErrors.filterKeys { key -> key != "email" }
                    },
                    label = { Text(context.getStringByKey(StringResources.EMAIL)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    isError = validationErrors.containsKey("email"),
                    supportingText = {
                        validationErrors["email"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("email")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        validationErrors = validationErrors.filterKeys { key -> key != "password" }
                    },
                    label = { Text(context.getStringByKey(StringResources.PASSWORD)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    isError = validationErrors.containsKey("password"),
                    supportingText = {
                        validationErrors["password"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            viewModel.login(email, password,
                                onSuccess = {
                                    navController.navigate("dashboard_screen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onFailure = { /* Error handled by viewModel */ }
                            )
                        } else {
                            Toast.makeText(context, context.getStringByKey(StringResources.ERROR_CORRECT_FORM_ERRORS), Toast.LENGTH_SHORT).show()
                        }
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
                        Text(context.getStringByKey(StringResources.LOGIN_BUTTON))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biometric Login Button
                if (viewModel.isBiometricAvailable()) {
                    OutlinedButton(
                        onClick = {
                            viewModel.biometricLogin(
                                onSuccess = {
                                    navController.navigate("dashboard_screen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onFailure = { /* Error handled by viewModel */ }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(context.getStringByKey(StringResources.BIOMETRIC_LOGIN))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register Link
                TextButton(
                    onClick = { navController.navigate("register_screen") }
                ) {
                    Text(context.getStringByKey(StringResources.NO_ACCOUNT))
                }
            }
        }
    }
}