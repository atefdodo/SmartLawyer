package com.smartlawyer.ui.screens.case


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.smartlawyer.ui.components.OutlinedDateField
import com.smartlawyer.ui.components.CaseType
import com.smartlawyer.ui.components.FormField
import com.smartlawyer.ui.components.DropdownFieldClient
import com.smartlawyer.ui.components.DropdownFieldCaseType
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.CaseViewModel
import com.smartlawyer.ui.viewmodels.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseRegistrationScreen(
    navController: NavController,
    caseViewModel: CaseViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form state variables
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

    var caseDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var caseImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Date picker state variables
    var showRegistrationDatePicker by remember { mutableStateOf(false) }
    var showFirstSessionDatePicker by remember { mutableStateOf(false) }

    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }

    // ViewModel states
    val clients by clientViewModel.clients.collectAsState()
    val isLoading by caseViewModel.isLoading.collectAsState()
    val errorMessage by caseViewModel.errorMessage.collectAsState()

    // Effects
    LaunchedEffect(Unit) {
        clientViewModel.loadClients()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            caseViewModel.clearError()
        }
    }

    // Form validation function
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (caseNumber.isBlank()) errors["caseNumber"] = "رقم القضية مطلوب"
        if (caseYear.isBlank()) errors["caseYear"] = "سنة القضية مطلوبة"
        else if (!caseYear.matches(Regex("^\\d{4}$"))) errors["caseYear"] = "سنة القضية يجب أن تكون 4 أرقام"
        if (registrationDate.isBlank()) errors["registrationDate"] = "تاريخ القيد مطلوب"
        if (selectedClient == null) errors["client"] = "اختيار العميل مطلوب"
        if (clientRole.isBlank()) errors["clientRole"] = "صفة العميل مطلوبة"
        if (opponentName.isBlank()) errors["opponentName"] = "اسم الخصم مطلوب"
        if (opponentRole.isBlank()) errors["opponentRole"] = "صفة الخصم مطلوبة"
        if (caseSubject.isBlank()) errors["caseSubject"] = "موضوع القضية مطلوب"
        if (courtName.isBlank()) errors["courtName"] = "اسم المحكمة مطلوب"

        validationErrors = errors
        return errors.isEmpty()
    }

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text("بيانات القضية", style = MaterialTheme.typography.titleMedium)

                FormField(
                    "رقم القضية",
                    caseNumber,
                    KeyboardType.Number,
                    Icons.Default.ConfirmationNumber,
                    validationErrors["caseNumber"],
                    validationErrors.containsKey("caseNumber")
                ) { caseNumber = it }

                FormField(
                    "سنة القضية",
                    caseYear,
                    KeyboardType.Number,
                    Icons.Default.DateRange,
                    validationErrors["caseYear"],
                    validationErrors.containsKey("caseYear")
                ) { caseYear = it }

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

                FormField(
                    "صفة العميل",
                    clientRole,
                    KeyboardType.Text,
                    Icons.Default.Person,
                    validationErrors["clientRole"],
                    validationErrors.containsKey("clientRole")
                ) { clientRole = it }

                Text("بيانات الخصم", style = MaterialTheme.typography.titleMedium)

                FormField(
                    "اسم الخصم",
                    opponentName,
                    KeyboardType.Text,
                    Icons.Default.Person,
                    validationErrors["opponentName"],
                    validationErrors.containsKey("opponentName")
                ) { opponentName = it }

                FormField(
                    "صفة الخصم",
                    opponentRole,
                    KeyboardType.Text,
                    Icons.Default.Badge,
                    validationErrors["opponentRole"],
                    validationErrors.containsKey("opponentRole")
                ) { opponentRole = it }

                Text("تفاصيل الجلسة", style = MaterialTheme.typography.titleMedium)

                FormField(
                    "موضوع القضية",
                    caseSubject,
                    KeyboardType.Text,
                    Icons.Default.Description,
                    validationErrors["caseSubject"],
                    validationErrors.containsKey("caseSubject")
                ) { caseSubject = it }

                FormField(
                    "اسم المحكمة",
                    courtName,
                    KeyboardType.Text,
                    Icons.Default.Gavel,
                    validationErrors["courtName"],
                    validationErrors.containsKey("courtName")
                ) { courtName = it }

                DropdownFieldCaseType("نوع القضية", selectedCaseType) { selectedCaseType = it }

                // Date Fields using OutlinedTextField
                OutlinedDateField(
                    label = "تاريخ القيد بالمحكمة",
                    value = registrationDate,
                    onClick = {
                        showRegistrationDatePicker = true
                    },
                    isError = validationErrors.containsKey("registrationDate"),
                    errorMessage = validationErrors["registrationDate"]
                )

                OutlinedDateField(
                    label = "تاريخ أول جلسة",
                    value = firstSessionDate,
                    onClick = {
                        showFirstSessionDatePicker = true
                    }
                )

                Text("المستندات", style = MaterialTheme.typography.titleMedium)

                FilePicker(
                    title = "رفع الملفات والمستندات",
                    documents = caseDocuments,
                    images = caseImages,
                    onDocumentsChanged = { caseDocuments = it },
                    onImagesChanged = { caseImages = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        if (validateForm()) {
                            val case = Case(
                                caseNumber = caseNumber,
                                caseYear = caseYear,
                                registrationDate = registrationDate,
                                clientId = selectedClient?.id ?: 0,
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
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ القضية")
                    }
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