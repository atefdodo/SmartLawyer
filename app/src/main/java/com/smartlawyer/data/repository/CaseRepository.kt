package com.smartlawyer.data.repository

import com.smartlawyer.data.dao.CaseDao
import com.smartlawyer.data.entities.Case
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CaseRepository @Inject constructor(
    private val caseDao: CaseDao
) {

    fun getAllCases(): Flow<List<Case>> = caseDao.getAllCases()

    fun getCasesByClientId(clientId: Long): Flow<List<Case>> = caseDao.getCasesByClientId(clientId)

    fun searchCases(query: String): Flow<List<Case>> = caseDao.searchCases(query) // 🔍

    suspend fun getCaseById(caseId: Long): Case? = caseDao.getCaseById(caseId)

    suspend fun insertCase(case: Case): Long = caseDao.insertCase(case)

    suspend fun insertCases(cases: List<Case>): List<Long> = caseDao.insertCases(cases) // 📥

    suspend fun updateCase(case: Case) = caseDao.updateCase(case)

    suspend fun deleteCase(case: Case) = caseDao.deleteCase(case)

    suspend fun deleteCases(cases: List<Case>) = caseDao.deleteCases(cases) // 🗑

    suspend fun getCaseCount(): Int = caseDao.getCaseCount()
}
