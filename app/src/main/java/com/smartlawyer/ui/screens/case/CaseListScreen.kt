package com.smartlawyer.ui.screens.case

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.data.entities.Case
import com.smartlawyer.ui.components.FileViewer
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.CaseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen for displaying list of cases in card format
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseListScreen(
    navController: NavController,
    caseViewModel: CaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCase by remember { mutableStateOf<Case?>(null) }
    var showCaseDetails by remember { mutableStateOf(false) }

    val cases by caseViewModel.cases.collectAsState()
    val isLoading by caseViewModel.isLoading.collectAsState()

    // Load cases on first launch
    LaunchedEffect(Unit) {
        caseViewModel.loadCases()
    }

    // Search cases when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            caseViewModel.loadCases()
        } else {
            caseViewModel.searchCases(searchQuery)
        }
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("قائمة القضايا") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("case_registration_screen") }) {
                            Icon(Icons.Default.Add, contentDescription = "إضافة قضية جديدة")
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
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("البحث في القضايا") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (cases.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "لا توجد قضايا" else "لا توجد نتائج للبحث",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            if (searchQuery.isBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { navController.navigate("case_registration_screen") }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("إضافة قضية جديدة")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cases) { case ->
                            CaseCard(
                                case = case,
                                onClick = {
                                    selectedCase = case
                                    showCaseDetails = true
                                }
                            )
                        }
                    }
                }
            }

            // Case Details Modal
            if (showCaseDetails && selectedCase != null) {
                CaseDetailsModal(
                    case = selectedCase!!,
                    onDismiss = {
                        showCaseDetails = false
                        selectedCase = null
                    }
                )
            }
        }
    }
}

/**
 * Card component for displaying case information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseCard(
    case: Case,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Case Number and Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رقم القضية: ${case.caseNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = case.caseType,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Court Information
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "محكمة",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = case.courtName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Next Session Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "تاريخ الجلسة",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الجلسة القادمة: ${case.firstSessionDate.ifBlank { "غير محدد" }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Case Subject
            Text(
                text = case.caseSubject,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modal for displaying detailed case information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsModal(
    case: Case,
    onDismiss: () -> Unit
) {
    val caseDocuments = FileUtils.jsonToPaths(case.documents)
    val caseImages = FileUtils.jsonToPaths(case.images)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("تفاصيل القضية")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("رقم القضية", case.caseNumber)
                DetailRow("سنة القضية", case.caseYear)
                DetailRow("تاريخ القيد", case.registrationDate)
                DetailRow("نوع القضية", case.caseType)
                DetailRow("اسم المحكمة", case.courtName)
                DetailRow("موضوع القضية", case.caseSubject)
                DetailRow("صفة العميل", case.clientRole)
                DetailRow("اسم الخصم", case.opponentName)
                DetailRow("صفة الخصم", case.opponentRole)
                DetailRow("تاريخ أول جلسة", case.firstSessionDate.ifBlank { "غير محدد" })
                
                // Files Section
                if (caseDocuments.isNotEmpty() || caseImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FileViewer(
                        title = "الملفات والمستندات",
                        documents = caseDocuments,
                        images = caseImages
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

/**
 * Helper component for displaying detail rows in modal
 */
@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
