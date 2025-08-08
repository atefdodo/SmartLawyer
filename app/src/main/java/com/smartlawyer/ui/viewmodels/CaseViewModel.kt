package com.smartlawyer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.entities.Client
import com.smartlawyer.data.repository.CaseRepository
import com.smartlawyer.data.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaseViewModel @Inject constructor(
    private val caseRepository: CaseRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _cases = MutableStateFlow<List<Case>>(emptyList())
    val cases: StateFlow<List<Case>> = _cases.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Current case for editing
    private val _currentCase = MutableStateFlow<Case?>(null)
    val currentCase: StateFlow<Case?> = _currentCase.asStateFlow()

    init {
        loadCases()
    }

    fun loadCases() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.getAllCases()
                    .catch { e ->
                        _errorMessage.value = "خطأ في تحميل القضايا: ${e.message}"
                    }
                    .collect { cases ->
                        _cases.value = cases
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل القضايا: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadCasesByClientId(clientId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.getCasesByClientId(clientId)
                    .catch { e ->
                        _errorMessage.value = "خطأ في تحميل قضايا العميل: ${e.message}"
                    }
                    .collect { cases ->
                        _cases.value = cases
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل قضايا العميل: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadCaseById(caseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val case = caseRepository.getCaseById(caseId)
                _currentCase.value = case
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل القضية: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchCases(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _searchQuery.value = query

            try {
                if (query.isBlank()) {
                    loadCases()
                    return@launch
                }

                caseRepository.searchCases(query)
                    .catch { e ->
                        _errorMessage.value = "خطأ في البحث: ${e.message}"
                        emit(emptyList())
                    }
                    .collect { results ->
                        _cases.value = results
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في البحث: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun addCase(case: Case, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.insertCase(case)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة القضية: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertCases(cases: List<Case>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.insertCases(cases)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إدخال القضايا: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCase(case: Case, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.updateCase(case)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث القضية: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCase(case: Case, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.deleteCase(case)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف القضية: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCases(cases: List<Case>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.deleteCases(cases)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف القضايا: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Load cases for a specific client and show success message
     */
    fun loadCasesForClient(clientId: Long, onSuccess: (Int) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.getCasesByClientId(clientId)
                    .catch { e ->
                        _errorMessage.value = "خطأ في تحميل قضايا العميل: ${e.message}"
                    }
                    .collect { cases ->
                        _cases.value = cases
                        _isLoading.value = false
                        onSuccess(cases.size)
                    }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل قضايا العميل: ${e.message}"
                _isLoading.value = false
            }
        }
    }


}
