package com.smartlawyer.ui.screens.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.data.entities.Client
import com.smartlawyer.ui.components.FilePicker
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.ClientViewModel

/**
 * Screen for registering new clients with enhanced validation and error handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientRegistrationScreen(
    navController: NavController,
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states with validation
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var powerOfAttorneyNumber by remember { mutableStateOf("") }

    // File states
    var clientDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var clientImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Validation states
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    val isLoading by clientViewModel.isLoading.collectAsState()
    val error by clientViewModel.error.collectAsState()

    // Show error message if any
    LaunchedEffect(error) {
        error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            clientViewModel.clearError()
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        val client = Client(
            name = name,
            address = address,
            phoneNumber = phoneNumber,
            email = email,
            powerOfAttorneyNumber = powerOfAttorneyNumber
        )
        
        val validationResult = clientViewModel.validateClient(client)
        
        // Convert validation result to UI error format
        val errors = mutableMapOf<String, String>()
        validationResult.errors.forEach { error ->
            when {
                error.contains("اسم العميل") -> errors["name"] = error
                error.contains("رقم الهاتف") -> errors["phoneNumber"] = error
                error.contains("البريد الإلكتروني") -> errors["email"] = error
            }
        }
        
        validationErrors = errors
        return validationResult.isValid
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تسجيل عميل جديد") },
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
                horizontalAlignment = Alignment.End
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        validationErrors = validationErrors.filterKeys { key -> key != "name" }
                    },
                    label = { Text("اسم العميل") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("name"),
                    supportingText = {
                        validationErrors["name"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("name")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Address Field
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("العنوان") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it
                        validationErrors = validationErrors.filterKeys { key -> key != "phoneNumber" }
                    },
                    label = { Text("رقم الهاتف") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    isError = validationErrors.containsKey("phoneNumber"),
                    supportingText = {
                        validationErrors["phoneNumber"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("phoneNumber")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        validationErrors = validationErrors.filterKeys { key -> key != "email" }
                    },
                    label = { Text("البريد الإلكتروني (اختياري)") },
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

                // Power of Attorney Number Field
                OutlinedTextField(
                    value = powerOfAttorneyNumber,
                    onValueChange = { powerOfAttorneyNumber = it },
                    label = { Text("رقم الوكالة (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // File Upload Section
                FilePicker(
                    title = "رفع الملفات والمستندات",
                    documents = clientDocuments,
                    images = clientImages,
                    onDocumentsChanged = { clientDocuments = it },
                    onImagesChanged = { clientImages = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            val client = Client(
                                name = name,
                                address = address,
                                phoneNumber = phoneNumber,
                                email = email,
                                powerOfAttorneyNumber = powerOfAttorneyNumber,
                                documents = FileUtils.pathsToJson(clientDocuments),
                                images = FileUtils.pathsToJson(clientImages)
                            )

                            clientViewModel.saveClient(client)
                            Toast.makeText(context, "تم تسجيل العميل بنجاح", Toast.LENGTH_SHORT).show()
                            navController.navigate("client_list_screen") {
                                popUpTo("client_registration_screen") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "يرجى تصحيح الأخطاء في النموذج", Toast.LENGTH_SHORT).show()
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
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ العميل")
                    }
                }
            }
        }
    }
} 