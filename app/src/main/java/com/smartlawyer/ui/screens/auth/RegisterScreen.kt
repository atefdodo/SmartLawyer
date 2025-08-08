package com.smartlawyer.ui.screens.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
 * Screen for user registration with enhanced validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validation states
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Google Sign-Up function
    fun handleGoogleSignUp() {
        viewModel.signUpWithGoogle(
            context = context,
            onSuccess = {
                navController.navigate("dashboard_screen") {
                    popUpTo(0) { inclusive = true }
                }
            },
            onFailure = { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        )
    }

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
        } else if (password.length < 6) {
            errors["password"] = context.getStringByKey(StringResources.ERROR_WEAK_PASSWORD)
        }

        // Confirm password validation
        if (confirmPassword.isBlank()) {
            errors["confirmPassword"] = context.getStringByKey(StringResources.ERROR_CONFIRM_PASSWORD_REQUIRED)
        } else if (password != confirmPassword) {
            errors["confirmPassword"] = context.getStringByKey(StringResources.ERROR_PASSWORDS_NOT_MATCH)
        }

        validationErrors = errors
        return errors.isEmpty()
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(context.getStringByKey(StringResources.REGISTER_TITLE)) },
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

                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        validationErrors = validationErrors.filterKeys { key -> key != "confirmPassword" }
                    },
                    label = { Text(context.getStringByKey(StringResources.CONFIRM_PASSWORD)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    isError = validationErrors.containsKey("confirmPassword"),
                    supportingText = {
                        validationErrors["confirmPassword"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Register Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            viewModel.register(email, password,
                                onSuccess = {
                                    Toast.makeText(context, context.getStringByKey(StringResources.SUCCESS_ACCOUNT_CREATED), Toast.LENGTH_SHORT).show()
                                    navController.navigate("dashboard_screen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onFailure = { error ->
                                    // Error is already handled by the ViewModel via errorMessage StateFlow
                                    // But you can add additional UI-specific error handling here if needed
                                }
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
                        Text(context.getStringByKey(StringResources.REGISTER_BUTTON))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-Up Button
                OutlinedButton(
                    onClick = { handleGoogleSignUp() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(context.getStringByKey(StringResources.GOOGLE_SIGN_UP))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Link
                TextButton(
                    onClick = { navController.navigate("login_screen") }
                ) {
                    Text(context.getStringByKey(StringResources.HAS_ACCOUNT))
                }
            }
        }
    }
}