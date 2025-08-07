package com.smartlawyer.ui.viewmodels

import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.repository.CaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CaseViewModelTest {

    @Mock
    private lateinit var caseRepository: CaseRepository

    private lateinit var caseViewModel: CaseViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        caseViewModel = CaseViewModel(caseRepository)
    }

    @After
    fun tearDown() {
        // Clean up any resources if needed
    }

    @Test
    fun `loadCases should update cases state when successful`() = runTest {
        // Given
        val mockCases = listOf(
            Case(
                id = 1,
                caseNumber = "123",
                caseYear = "2024",
                registrationDate = "2024-01-01",
                clientId = 1,
                clientRole = "مدعي",
                opponentName = "خصم",
                opponentRole = "مدعى عليه",
                caseSubject = "قضية تجارية",
                courtName = "محكمة تجارية",
                caseType = "مدني جزئي",
                firstSessionDate = "2024-02-01"
            )
        )
        `when`(caseRepository.getAllCases()).thenReturn(flowOf(mockCases))

        // When
        caseViewModel.loadCases()

        // Then
        assert(caseViewModel.cases.value == mockCases)
        assert(!caseViewModel.isLoading.value)
        assert(caseViewModel.errorMessage.value == null)
    }

    @Test
    fun `loadCases should handle error properly`() = runTest {
        // Given
        val errorMessage = "Database error"
        `when`(caseRepository.getAllCases()).thenThrow(RuntimeException(errorMessage))

        // When
        caseViewModel.loadCases()

        // Then
        assert(caseViewModel.cases.value.isEmpty())
        assert(!caseViewModel.isLoading.value)
        assert(caseViewModel.errorMessage.value?.contains("خطأ في تحميل القضايا") == true)
    }

    @Test
    fun `addCase should call repository and handle success`() = runTest {
        // Given
        val caseToAdd = Case(
            caseNumber = "456",
            caseYear = "2024",
            registrationDate = "2024-01-01",
            clientId = 1,
            clientRole = "مدعي",
            opponentName = "خصم",
            opponentRole = "مدعى عليه",
            caseSubject = "قضية مدنية",
            courtName = "محكمة مدنية",
            caseType = "مدني كلي",
            firstSessionDate = "2024-02-01"
        )
        var onSuccessCalled = false

        // When
        caseViewModel.addCase(caseToAdd) {
            onSuccessCalled = true
        }

        // Then
        verify(caseRepository).insertCase(caseToAdd)
        assert(onSuccessCalled)
        assert(!caseViewModel.isLoading.value)
        assert(caseViewModel.errorMessage.value == null)
    }

    @Test
    fun `addCase should handle error properly`() = runTest {
        // Given
        val caseToAdd = Case(
            caseNumber = "789",
            caseYear = "2024",
            registrationDate = "2024-01-01",
            clientId = 1,
            clientRole = "مدعي",
            opponentName = "خصم",
            opponentRole = "مدعى عليه",
            caseSubject = "قضية مدنية",
            courtName = "محكمة مدنية",
            caseType = "مدني كلي",
            firstSessionDate = "2024-02-01"
        )
        val errorMessage = "Insert failed"
        `when`(caseRepository.insertCase(caseToAdd)).thenThrow(RuntimeException(errorMessage))

        // When
        caseViewModel.addCase(caseToAdd)

        // Then
        verify(caseRepository).insertCase(caseToAdd)
        assert(!caseViewModel.isLoading.value)
        assert(caseViewModel.errorMessage.value?.contains("خطأ في إضافة القضية") == true)
    }

    @Test
    fun `clearError should reset error message`() = runTest {
        // Given
        caseViewModel.clearError()

        // When & Then
        assert(caseViewModel.errorMessage.value == null)
    }

    @Test
    fun `searchCases should call repository with query`() = runTest {
        // Given
        val query = "قضية"
        val mockResults = listOf(
            Case(
                id = 1,
                caseNumber = "123",
                caseYear = "2024",
                registrationDate = "2024-01-01",
                clientId = 1,
                clientRole = "مدعي",
                opponentName = "خصم",
                opponentRole = "مدعى عليه",
                caseSubject = "قضية تجارية",
                courtName = "محكمة تجارية",
                caseType = "مدني جزئي",
                firstSessionDate = "2024-02-01"
            )
        )
        `when`(caseRepository.searchCases(query)).thenReturn(flowOf(mockResults))

        // When
        caseViewModel.searchCases(query)

        // Then
        verify(caseRepository).searchCases(query)
        assert(caseViewModel.cases.value == mockResults)
        assert(!caseViewModel.isLoading.value)
    }
}
