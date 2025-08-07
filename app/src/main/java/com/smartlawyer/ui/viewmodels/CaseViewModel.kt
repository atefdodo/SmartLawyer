package com.smartlawyer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaseViewModel @Inject constructor(
    private val caseRepository: CaseRepository
) : ViewModel() {

    private val _cases = MutableStateFlow<List<Case>>(emptyList())
    val cases: StateFlow<List<Case>> = _cases.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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

    fun searchCases(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                caseRepository.searchCases(query)
                    .catch { e ->
                        _errorMessage.value = "خطأ في البحث: ${e.message}"
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
}
