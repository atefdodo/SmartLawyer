package com.smartlawyer.ui.screens.case

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.smartlawyer.navigation.Screens
import com.smartlawyer.ui.components.FileViewer
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.CaseViewModel


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
    var selectedCase by remember { mutableStateOf<Case?>(null) }
    var showCaseDetails by remember { mutableStateOf(false) }
    var caseToDelete by remember { mutableStateOf<Case?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Bulk operations state
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedCases by remember { mutableStateOf(setOf<Case>()) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    


    val cases by caseViewModel.cases.collectAsState()
    val isLoading by caseViewModel.isLoading.collectAsState()
    val searchQuery by caseViewModel.searchQuery.collectAsState()
    val error by caseViewModel.errorMessage.collectAsState()

    // Handle errors from ViewModel
    LaunchedEffect(error) {
        error?.let { errorMsg ->
            // You can show a toast or dialog here
            println("CaseListScreen: Error - $errorMsg")
            caseViewModel.clearError()
        }
    }

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
                        // Bulk operations
                        if (isMultiSelectMode) {
                            // Cancel multi-select
                            IconButton(onClick = { 
                                isMultiSelectMode = false
                                selectedCases = setOf()
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "إلغاء التحديد المتعدد"
                                )
                            }
                            // Bulk delete
                            if (selectedCases.isNotEmpty()) {
                                IconButton(onClick = { showBulkDeleteDialog = true }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف المحدد",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            // Multi-select mode
                            IconButton(onClick = { isMultiSelectMode = true }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "وضع التحديد المتعدد"
                                )
                            }

                            IconButton(onClick = { navController.navigate("case_registration_screen") }) {
                                Icon(Icons.Default.Add, contentDescription = "إضافة قضية جديدة")
                            }
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
                    onValueChange = { 
                        caseViewModel.updateSearchQuery(it)
                        caseViewModel.searchCases(it)
                    },
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
                    isMultiSelectMode = isMultiSelectMode,
                    isSelected = selectedCases.contains(case),
                    onClick = {
                        if (isMultiSelectMode) {
                            selectedCases = if (selectedCases.contains(case)) {
                                selectedCases - case
                            } else {
                                selectedCases + case
                            }
                        } else {
                            selectedCase = case
                            showCaseDetails = true
                        }
                    },
                    onEdit = {
                        navController.navigate(Screens.CaseEdit.createRoute(case.id))
                    },
                    onDelete = {
                        caseToDelete = case
                        showDeleteDialog = true
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

            // Delete Confirmation Dialog
            if (showDeleteDialog && caseToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        caseToDelete = null
                    },
                    title = {
                        Text("تأكيد الحذف")
                    },
                    text = {
                        Text("هل أنت متأكد من حذف القضية رقم ${caseToDelete!!.caseNumber}؟")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                caseToDelete?.let { case ->
                                    caseViewModel.deleteCase(case) {
                                        // Case deleted successfully
                                        Toast.makeText(context, "تم حذف القضية بنجاح", Toast.LENGTH_SHORT).show()
                                        showDeleteDialog = false
                                        caseToDelete = null
                                    }
                                }
                            }
                        ) {
                            Text("حذف", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                caseToDelete = null
                            }
                        ) {
                            Text("إلغاء")
                        }
                    }
                )
            }

            // Bulk Delete Confirmation Dialog
            if (showBulkDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showBulkDeleteDialog = false },
                    title = { Text("تأكيد الحذف المتعدد") },
                    text = { Text("هل أنت متأكد من حذف ${selectedCases.size} قضية محددة؟") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                caseViewModel.deleteCases(selectedCases.toList()) {
                                    Toast.makeText(context, "تم حذف ${selectedCases.size} قضية بنجاح", Toast.LENGTH_SHORT).show()
                                    showBulkDeleteDialog = false
                                    isMultiSelectMode = false
                                    selectedCases = setOf()
                                }
                            }
                        ) {
                            Text("حذف", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBulkDeleteDialog = false }) {
                            Text("إلغاء")
                        }
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
    isMultiSelectMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Selection indicator for multi-select mode
            if (isMultiSelectMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Edit else Icons.Default.Edit,
                        contentDescription = if (isSelected) "محدد" else "غير محدد",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
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
