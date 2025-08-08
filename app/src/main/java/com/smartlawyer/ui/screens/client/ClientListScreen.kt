package com.smartlawyer.ui.screens.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartlawyer.R
import com.smartlawyer.data.entities.Client
import com.smartlawyer.ui.components.FileViewer
import com.smartlawyer.ui.components.ErrorDialog
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.viewmodels.ClientViewModel

/**
 * Screen for displaying list of clients in card format
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    navController: NavController,
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    // State variables
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var showClientDetails by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var clientToDelete by remember { mutableStateOf<Client?>(null) }

    // Collect ViewModel states
    val clients by clientViewModel.clients.collectAsState()
    val isLoading by clientViewModel.isLoading.collectAsState()
    val error by clientViewModel.error.collectAsState()
    val searchQuery by clientViewModel.searchQuery.collectAsState()



    // Handle errors from ViewModel
    LaunchedEffect(error) {
        if (error != null) {
            errorMessage = error
            showError = true
        }
    }

    // Load clients on first launch
    LaunchedEffect(Unit) {
        clientViewModel.loadClients()
    }

    // Search clients when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            clientViewModel.loadClients()
        } else {
            clientViewModel.searchClients(searchQuery)
        }
    }

    // Clean up state when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            selectedClient = null
            showClientDetails = false
        }
    }

    // Layout direction RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.client_list_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("client_registration_screen") }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_new_client)
                            )
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
                        clientViewModel.updateSearchQuery(it)
                        clientViewModel.searchClients(it)
                    },
                    label = { Text(stringResource(R.string.search_clients)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = stringResource(R.string.loading_clients),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    clients.isEmpty() -> {
                        EmptyClientsView(
                            searchQuery = searchQuery,
                            onAddClient = { navController.navigate("client_registration_screen") }
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = clients,
                                key = { client -> client.id }
                            ) { client ->
                                ClientCard(
                                    client = client,
                                    onClick = {
                                        selectedClient = client
                                        showClientDetails = true
                                    },
                                    onDelete = {
                                        clientToDelete = client
                                        showDeleteConfirmation = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Client Details Modal
            if (showClientDetails && selectedClient != null) {
                ClientDetailsModal(
                    client = selectedClient!!,
                    onDismiss = {
                        showClientDetails = false
                        selectedClient = null
                    }
                )
            }

            // Error Dialog
            if (showError && errorMessage != null) {
                ErrorDialog(
                    message = errorMessage!!,
                    onDismiss = {
                        showError = false
                        errorMessage = null
                        clientViewModel.clearError()
                    }
                )
            }

            // Delete Confirmation Dialog
            if (showDeleteConfirmation && clientToDelete != null) {
                AlertDialog(
                    onDismissRequest = { 
                        showDeleteConfirmation = false
                        clientToDelete = null
                    },
                    title = { Text(stringResource(R.string.delete_client_title)) },
                    text = { 
                        Text(
                            stringResource(R.string.delete_client_confirmation, clientToDelete!!.name)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                clientToDelete?.let { client ->
                                    clientViewModel.deleteClient(client)
                                }
                                showDeleteConfirmation = false
                                clientToDelete = null
                            }
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { 
                                showDeleteConfirmation = false
                                clientToDelete = null
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

/**
 * Empty state view for when no clients are found
 */
@Composable
private fun EmptyClientsView(
    searchQuery: String,
    onAddClient: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (searchQuery.isBlank())
                    stringResource(R.string.no_clients_found)
                else
                    stringResource(R.string.no_search_results),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (searchQuery.isBlank()) {
                Button(
                    onClick = onAddClient,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_new_client))
                }
            }
        }
    }
}

/**
 * Card component for displaying client information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCard(
    client: Client,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Client info (clickable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onClick() },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
            // Client Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.client),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Phone Number
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = stringResource(R.string.phone),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = client.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Power of Attorney Number (if available)
            if (client.powerOfAttorneyNumber.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment, // Fixed: Using appropriate icon
                        contentDescription = stringResource(R.string.power_of_attorney_number),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.power_of_attorney_format, client.powerOfAttorneyNumber),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Additional client info if available
            if (client.address.isNotBlank() || client.email.isNotBlank()) {
                ClientAdditionalInfo(
                    address = client.address,
                    email = client.email
                )
            }
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_client),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Component for displaying additional client information
 */
@Composable
private fun ClientAdditionalInfo(
    address: String,
    email: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (address.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.client_address),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (email.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(R.string.email),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Modal for displaying detailed client information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsModal(
    client: Client,
    onDismiss: () -> Unit
) {
    val clientDocuments = remember { FileUtils.jsonToPaths(client.documents) }
    val clientImages = remember { FileUtils.jsonToPaths(client.images) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.client_details))
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DetailRow(stringResource(R.string.client_name), client.name)
                }
                item {
                    DetailRow(stringResource(R.string.phone_number), client.phoneNumber)
                }
                if (client.powerOfAttorneyNumber.isNotBlank()) {
                    item {
                        DetailRow(
                            stringResource(R.string.power_of_attorney_number),
                            client.powerOfAttorneyNumber
                        )
                    }
                }
                if (client.address.isNotBlank()) {
                    item {
                        DetailRow(stringResource(R.string.client_address), client.address)
                    }
                }
                if (client.email.isNotBlank()) {
                    item {
                        DetailRow(stringResource(R.string.email), client.email)
                    }
                }

                // Files Section
                if (clientDocuments.isNotEmpty() || clientImages.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        FileViewer(
                            title = stringResource(R.string.files_documents),
                            documents = clientDocuments,
                            images = clientImages
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

/**
 * Component for displaying detail rows in the modal
 */
@Composable
private fun DetailRow(
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
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}