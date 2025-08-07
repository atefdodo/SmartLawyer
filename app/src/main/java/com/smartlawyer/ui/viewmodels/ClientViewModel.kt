package com.smartlawyer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartlawyer.data.entities.Client
import com.smartlawyer.data.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing client data and UI state
 */
@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Load all clients from repository
     */
    fun loadClients() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                clientRepository.getAllClients()
                    .catch { exception ->
                        println("ClientViewModel: Error loading clients: ${exception.message}")
                        _error.value = handleException(exception)
                        emit(emptyList())
                    }
                    .collect { clientList ->
                        println("ClientViewModel: Received ${clientList.size} clients")
                        _clients.value = clientList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                println("ClientViewModel: Exception in loadClients: ${e.message}")
                _error.value = handleException(e)
                _isLoading.value = false
            }
        }
    }

    /**
     * Search clients by query
     */
    fun searchClients(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _searchQuery.value = query

                if (query.isBlank()) {
                    loadClients()
                    return@launch
                }

                clientRepository.searchClients(query)
                    .catch { exception ->
                        _error.value = handleException(exception)
                        emit(emptyList())
                    }
                    .collect { clientList ->
                        _clients.value = clientList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = handleException(e)
                _isLoading.value = false
            }
        }
    }



    /**
     * Save or update a client
     */
    fun saveClient(client: Client) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (client.id == 0L) {
                    clientRepository.insertClient(client)
                } else {
                    clientRepository.updateClient(client)
                }

                // Refresh the client list
                loadClients()
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a client
     */
    fun deleteClient(client: Client) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                clientRepository.deleteClient(client)

                // Refresh the client list
                loadClients()
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }



    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }



    /**
     * Add test client for debugging
     */
    fun addTestClient() {
        viewModelScope.launch {
            try {
                val testClient = Client(
                    name = "عميل تجريبي",
                    address = "عنوان تجريبي",
                    phoneNumber = "0123456789",
                    email = "test@example.com",
                    powerOfAttorneyNumber = "12345"
                )
                clientRepository.insertClient(testClient)
                println("ClientViewModel: Test client added successfully")
            } catch (e: Exception) {
                println("ClientViewModel: Error adding test client: ${e.message}")
                _error.value = handleException(e)
            }
        }
    }

    /**
     * Handle exceptions and return user-friendly error messages
     */
    private fun handleException(exception: Throwable): String {
        return when (exception) {
            is java.net.UnknownHostException -> "خطأ في الاتصال بالشبكة"
            is java.net.SocketTimeoutException -> "انتهت مهلة الاتصال"
            is java.io.IOException -> "خطأ في عملية الإدخال/الإخراج"
            is IllegalArgumentException -> "بيانات غير صالحة"
            is IllegalStateException -> "حالة التطبيق غير صحيحة"
            is SecurityException -> "ليس لديك الصلاحية للوصول لهذه البيانات"
            else -> exception.message ?: "حدث خطأ غير متوقع"
        }
    }

    /**
     * Validate client data
     */
    fun validateClient(client: Client): ValidationResult {
        val errors = mutableListOf<String>()

        if (client.name.isBlank()) {
            errors.add("اسم العميل مطلوب")
        }

        if (client.phoneNumber.isBlank()) {
            errors.add("رقم الهاتف مطلوب")
        } else if (!isValidPhoneNumber(client.phoneNumber)) {
            errors.add("رقم الهاتف غير صالح")
        }

        if (client.email.isNotBlank() && !isValidEmail(client.email)) {
            errors.add("البريد الإلكتروني غير صالح")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    /**
     * Validate phone number format
     */
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^[+]?[0-9]{10,15}$")
        return phoneRegex.matches(phoneNumber.replace("\\s".toRegex(), ""))
    }

    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    /**
     * Data class for validation results
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )
}