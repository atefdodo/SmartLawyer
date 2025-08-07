package com.smartlawyer.ui.screens.case

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.entities.Client
import com.smartlawyer.ui.components.FilePicker
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.CaseViewModel
import com.smartlawyer.ui.viewmodels.ClientViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Screen for registering new cases with enhanced validation and error handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseRegistrationScreen(
    navController: NavController,
    caseViewModel: CaseViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states with validation
    var caseNumber by remember { mutableStateOf("") }
    var caseYear by remember { mutableStateOf("") }
    var registrationDate by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var clientRole by remember { mutableStateOf("") }
    var opponentName by remember { mutableStateOf("") }
    var opponentRole by remember { mutableStateOf("") }
    var caseSubject by remember { mutableStateOf("") }
    var courtName by remember { mutableStateOf("") }
    var selectedCaseType by remember { mutableStateOf(CaseType.CIVIL_PARTIAL) }
    var firstSessionDate by remember { mutableStateOf("") }

    // File states
    var caseDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var caseImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // UI states
    var registrationDatePickerOpen by remember { mutableStateOf(false) }
    var firstSessionDatePickerOpen by remember { mutableStateOf(false) }
    var clientExpanded by remember { mutableStateOf(false) }
    var caseTypeExpanded by remember { mutableStateOf(false) }

    // Validation states
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    val clients by clientViewModel.clients.collectAsState()
    val isLoading by caseViewModel.isLoading.collectAsState()
    val errorMessage by caseViewModel.errorMessage.collectAsState()

    // Load clients initially
    LaunchedEffect(Unit) {
        clientViewModel.loadClients()
    }

    // Show error message if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            caseViewModel.clearError()
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()
        
        if (caseNumber.isBlank()) {
            errors["caseNumber"] = "رقم القضية مطلوب"
        }
        
        if (caseYear.isBlank()) {
            errors["caseYear"] = "سنة القضية مطلوبة"
        } else if (!caseYear.matches(Regex("^\\d{4}$"))) {
            errors["caseYear"] = "سنة القضية يجب أن تكون 4 أرقام"
        }
        
        if (registrationDate.isBlank()) {
            errors["registrationDate"] = "تاريخ القيد مطلوب"
        }
        
        if (selectedClient == null) {
            errors["client"] = "اختيار العميل مطلوب"
        }
        
        if (clientRole.isBlank()) {
            errors["clientRole"] = "صفة العميل مطلوبة"
        }
        
        if (opponentName.isBlank()) {
            errors["opponentName"] = "اسم الخصم مطلوب"
        }
        
        if (opponentRole.isBlank()) {
            errors["opponentRole"] = "صفة الخصم مطلوبة"
        }
        
        if (caseSubject.isBlank()) {
            errors["caseSubject"] = "موضوع القضية مطلوب"
        }
        
        if (courtName.isBlank()) {
            errors["courtName"] = "اسم المحكمة مطلوب"
        }
        
        validationErrors = errors
        return errors.isEmpty()
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تسجيل قضية جديدة") },
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
                // Case Number Field
                OutlinedTextField(
                    value = caseNumber,
                    onValueChange = { 
                        caseNumber = it
                        validationErrors = validationErrors.filterKeys { key -> key != "caseNumber" }
                    },
                    label = { Text("رقم القضية") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = validationErrors.containsKey("caseNumber"),
                    supportingText = {
                        validationErrors["caseNumber"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("caseNumber")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Case Year Field
                OutlinedTextField(
                    value = caseYear,
                    onValueChange = { 
                        caseYear = it
                        validationErrors = validationErrors.filterKeys { key -> key != "caseYear" }
                    },
                    label = { Text("سنة القضية") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = validationErrors.containsKey("caseYear"),
                    supportingText = {
                        validationErrors["caseYear"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("caseYear")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Registration Date Field
                OutlinedTextField(
                    value = registrationDate,
                    onValueChange = {},
                    label = { Text("تاريخ القيد بالمحكمة") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { registrationDatePickerOpen = true },
                    isError = validationErrors.containsKey("registrationDate"),
                    supportingText = {
                        validationErrors["registrationDate"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        if (validationErrors.containsKey("registrationDate")) {
                            Icon(Icons.Default.Error, contentDescription = "خطأ", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Client Dropdown
                ExposedDropdownMenuBox(
                    expanded = clientExpanded,
                    onExpandedChange = { clientExpanded = !clientExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedClient?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اختيار العميل") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(clientExpanded) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = validationErrors.containsKey("client"),
                        supportingText = {
                            validationErrors["client"]?.let { error ->
                                Text(error, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = clientExpanded,
                        onDismissRequest = { clientExpanded = false }
                    ) {
                        clients.forEach { client ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(client.name)
                                        Text(client.phoneNumber, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedClient = client
                                    clientExpanded = false
                                    validationErrors = validationErrors.filterKeys { key -> key != "client" }
                                }
                            )
                        }
                    }
                }

                // Client Role Field
                OutlinedTextField(
                    value = clientRole,
                    onValueChange = { 
                        clientRole = it
                        validationErrors = validationErrors.filterKeys { key -> key != "clientRole" }
                    },
                    label = { Text("صفة العميل") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("clientRole"),
                    supportingText = {
                        validationErrors["clientRole"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Opponent Name Field
                OutlinedTextField(
                    value = opponentName,
                    onValueChange = { 
                        opponentName = it
                        validationErrors = validationErrors.filterKeys { key -> key != "opponentName" }
                    },
                    label = { Text("اسم الخصم") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("opponentName"),
                    supportingText = {
                        validationErrors["opponentName"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Opponent Role Field
                OutlinedTextField(
                    value = opponentRole,
                    onValueChange = { 
                        opponentRole = it
                        validationErrors = validationErrors.filterKeys { key -> key != "opponentRole" }
                    },
                    label = { Text("صفة الخصم") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("opponentRole"),
                    supportingText = {
                        validationErrors["opponentRole"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Case Subject Field
                OutlinedTextField(
                    value = caseSubject,
                    onValueChange = { 
                        caseSubject = it
                        validationErrors = validationErrors.filterKeys { key -> key != "caseSubject" }
                    },
                    label = { Text("موضوع القضية") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("caseSubject"),
                    supportingText = {
                        validationErrors["caseSubject"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Court Name Field
                OutlinedTextField(
                    value = courtName,
                    onValueChange = { 
                        courtName = it
                        validationErrors = validationErrors.filterKeys { key -> key != "courtName" }
                    },
                    label = { Text("اسم المحكمة") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = validationErrors.containsKey("courtName"),
                    supportingText = {
                        validationErrors["courtName"]?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Case type dropdown
                ExposedDropdownMenuBox(
                    expanded = caseTypeExpanded,
                    onExpandedChange = { caseTypeExpanded = !caseTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCaseType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع القضية") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(caseTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = caseTypeExpanded,
                        onDismissRequest = { caseTypeExpanded = false }
                    ) {
                        CaseType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedCaseType = type
                                    caseTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                // First Session Date Field
                OutlinedTextField(
                    value = firstSessionDate,
                    onValueChange = {},
                    label = { Text("تاريخ أول جلسة") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { firstSessionDatePickerOpen = true }
                )

                // File Upload Section
                FilePicker(
                    title = "رفع الملفات والمستندات",
                    documents = caseDocuments,
                    images = caseImages,
                    onDocumentsChanged = { caseDocuments = it },
                    onImagesChanged = { caseImages = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            val case = Case(
                                caseNumber = caseNumber,
                                caseYear = caseYear,
                                registrationDate = registrationDate,
                                clientId = selectedClient!!.id,
                                clientRole = clientRole,
                                opponentName = opponentName,
                                opponentRole = opponentRole,
                                caseSubject = caseSubject,
                                courtName = courtName,
                                caseType = selectedCaseType.value,
                                firstSessionDate = firstSessionDate,
                                documents = FileUtils.pathsToJson(caseDocuments),
                                images = FileUtils.pathsToJson(caseImages)
                            )

                            caseViewModel.addCase(case) {
                                Toast.makeText(context, "تم تسجيل القضية بنجاح", Toast.LENGTH_SHORT).show()
                                navController.navigate("case_list_screen") {
                                    popUpTo("case_registration_screen") { inclusive = true }
                                }
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
                        Text("حفظ القضية")
                    }
                }
            }

            // Show Date Pickers
            if (registrationDatePickerOpen) {
                ShowDatePickerDialog(
                    onDateSelected = {
                        registrationDate = it
                        registrationDatePickerOpen = false
                        validationErrors = validationErrors.filterKeys { key -> key != "registrationDate" }
                    },
                    onDismiss = { registrationDatePickerOpen = false }
                )
            }

            if (firstSessionDatePickerOpen) {
                ShowDatePickerDialog(
                    onDateSelected = {
                        firstSessionDate = it
                        firstSessionDatePickerOpen = false
                    },
                    onDismiss = { firstSessionDatePickerOpen = false }
                )
            }
        }
    }
}

/**
 * Enum for case types with proper localization
 */
enum class CaseType(val value: String, val displayName: String) {
    CIVIL_PARTIAL("مدني جزئي", "مدني جزئي"),
    CIVIL_FULL("مدني كلي", "مدني كلي"),
    LABOR("عمالي", "عمالي"),
    LABOR_APPEAL("استئناف عمالي", "استئناف عمالي"),
    MISDEMEANOR("جنحة", "جنحة"),
    FELONY("جناية", "جناية"),
    ECONOMIC("اقتصادي", "اقتصادي"),
    ADMINISTRATIVE("محكمة إدارية", "محكمة إدارية"),
    HOUSING("إسكان", "إسكان"),
    CASSATION("نقض", "نقض")
}

/**
 * Enhanced date picker with proper Arabic localization and date formatting
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePickerDialog(
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    // Use proper date format
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.forLanguageTag("ar-EG"))

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val selected = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(formatter)
                    onDateSelected(selected)
                }
            }) {
                Text("اختيار")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
