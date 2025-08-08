package com.smartlawyer.ui.screens.case

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.entities.Client
import com.smartlawyer.ui.components.FilePicker
import com.smartlawyer.ui.components.ShowDatePickerDialog
import com.smartlawyer.ui.components.CaseType
import com.smartlawyer.ui.components.FormField
import com.smartlawyer.ui.components.DropdownFieldClient
import com.smartlawyer.ui.components.DropdownFieldCaseType
import com.smartlawyer.ui.components.OutlinedDateField
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.CaseViewModel
import com.smartlawyer.ui.viewmodels.ClientViewModel




/**
 * Screen for editing existing cases
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseEditScreen(
    navController: NavController,
    caseId: Long,
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

    // Date picker state variables
    var showRegistrationDatePicker by remember { mutableStateOf(false) }
    var showFirstSessionDatePicker by remember { mutableStateOf(false) }

    // Validation states
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    val clients by clientViewModel.clients.collectAsState()
    val isLoading by caseViewModel.isLoading.collectAsState()
    val errorMessage by caseViewModel.errorMessage.collectAsState()

    // Load clients and case data initially
    LaunchedEffect(Unit) {
        clientViewModel.loadClients()
        caseViewModel.loadCaseById(caseId)
    }

    val currentCase by caseViewModel.currentCase.collectAsState()

    // Load case data when available
    LaunchedEffect(currentCase) {
        currentCase?.let { case ->
            caseNumber = case.caseNumber
            caseYear = case.caseYear
            registrationDate = case.registrationDate
            clientRole = case.clientRole
            opponentName = case.opponentName
            opponentRole = case.opponentRole
            caseSubject = case.caseSubject
            courtName = case.courtName
            firstSessionDate = case.firstSessionDate
            
            // Set case type
            selectedCaseType = CaseType.fromValue(case.caseType)
            
            // Set documents and images
            caseDocuments = FileUtils.jsonToPaths(case.documents)
            caseImages = FileUtils.jsonToPaths(case.images)
        }
    }

    // Set client when both case and clients are loaded
    LaunchedEffect(currentCase, clients) {
        currentCase?.let { case ->
            selectedClient = clients.find { it.id == case.clientId }
        }
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
                    title = { Text("تعديل القضية") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
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
                Text("بيانات القضية", style = MaterialTheme.typography.titleMedium)

                FormField("رقم القضية", caseNumber, KeyboardType.Number, Icons.Default.ConfirmationNumber, validationErrors["caseNumber"], validationErrors.containsKey("caseNumber")) { 
                    caseNumber = it
                    validationErrors = validationErrors.filterKeys { key -> key != "caseNumber" }
                }
                FormField("سنة القضية", caseYear, KeyboardType.Number, Icons.Default.DateRange, validationErrors["caseYear"], validationErrors.containsKey("caseYear")) { 
                    caseYear = it
                    validationErrors = validationErrors.filterKeys { key -> key != "caseYear" }
                }

                OutlinedDateField(
                    label = "تاريخ القيد بالمحكمة",
                    value = registrationDate,
                    onClick = {
                        showRegistrationDatePicker = true
                    },
                    isError = validationErrors.containsKey("registrationDate"),
                    errorMessage = validationErrors["registrationDate"]
                )

                DropdownFieldClient(
                    label = "اختيار العميل",
                    selectedClient = selectedClient,
                    clients = clients,
                    onClientSelected = {
                        selectedClient = it
                        validationErrors = validationErrors.filterKeys { key -> key != "client" }
                    },
                    isError = validationErrors.containsKey("client"),
                    errorMessage = validationErrors["client"]
                )

                FormField("صفة العميل", clientRole, KeyboardType.Text, Icons.Default.Person, validationErrors["clientRole"], validationErrors.containsKey("clientRole")) { 
                    clientRole = it
                    validationErrors = validationErrors.filterKeys { key -> key != "clientRole" }
                }

                Text("بيانات الخصم", style = MaterialTheme.typography.titleMedium)

                FormField("اسم الخصم", opponentName, KeyboardType.Text, Icons.Default.Person, validationErrors["opponentName"], validationErrors.containsKey("opponentName")) { 
                    opponentName = it
                    validationErrors = validationErrors.filterKeys { key -> key != "opponentName" }
                }
                FormField("صفة الخصم", opponentRole, KeyboardType.Text, Icons.Default.Badge, validationErrors["opponentRole"], validationErrors.containsKey("opponentRole")) { 
                    opponentRole = it
                    validationErrors = validationErrors.filterKeys { key -> key != "opponentRole" }
                }

                Text("تفاصيل الجلسة", style = MaterialTheme.typography.titleMedium)

                FormField("موضوع القضية", caseSubject, KeyboardType.Text, Icons.Default.Description, validationErrors["caseSubject"], validationErrors.containsKey("caseSubject")) { 
                    caseSubject = it
                    validationErrors = validationErrors.filterKeys { key -> key != "caseSubject" }
                }
                FormField("اسم المحكمة", courtName, KeyboardType.Text, Icons.Default.Gavel, validationErrors["courtName"], validationErrors.containsKey("courtName")) { 
                    courtName = it
                    validationErrors = validationErrors.filterKeys { key -> key != "courtName" }
                }

                DropdownFieldCaseType("نوع القضية", selectedCaseType) { selectedCaseType = it }

                OutlinedDateField(
                    label = "تاريخ أول جلسة",
                    value = firstSessionDate,
                    onClick = {
                        showFirstSessionDatePicker = true
                    }
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

                // Update Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            val updatedCase = Case(
                                id = caseId,
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

                            caseViewModel.updateCase(updatedCase) {
                                Toast.makeText(context, "تم تحديث القضية بنجاح", Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
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
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ التعديلات")
                    }
                }
            }

    }

    // Date Picker Dialogs
    if (showRegistrationDatePicker) {
        ShowDatePickerDialog(
            initialDate = registrationDate.ifEmpty { null },
            onDateSelected = { selectedDate ->
                registrationDate = selectedDate
                validationErrors = validationErrors.filterKeys { key -> key != "registrationDate" }
            },
            onDismiss = {
                showRegistrationDatePicker = false
            }
        )
    }

    if (showFirstSessionDatePicker) {
        ShowDatePickerDialog(
            initialDate = firstSessionDate.ifEmpty { null },
            onDateSelected = { selectedDate ->
                firstSessionDate = selectedDate
            },
            onDismiss = {
                showFirstSessionDatePicker = false
            }
        )
    }
    }
}
