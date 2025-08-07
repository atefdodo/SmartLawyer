package com.smartlawyer.data.dao

import androidx.room.*
import com.smartlawyer.data.entities.Case
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    @Query("SELECT * FROM cases ORDER BY registrationDate DESC")
    fun getAllCases(): Flow<List<Case>>

    @Query("SELECT * FROM cases WHERE id = :caseId")
    suspend fun getCaseById(caseId: Long): Case?

    @Query("SELECT * FROM cases WHERE clientId = :clientId")
    fun getCasesByClientId(clientId: Long): Flow<List<Case>>

    @Insert
    suspend fun insertCase(case: Case): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCases(cases: List<Case>): List<Long> // ✅ Batch insert

    @Update
    suspend fun updateCase(case: Case)

    @Delete
    suspend fun deleteCase(case: Case)

    @Delete
    suspend fun deleteCases(cases: List<Case>) // ✅ Batch delete

    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCaseCount(): Int

    @Query("""
        SELECT * FROM cases 
        WHERE caseNumber LIKE '%' || :query || '%' 
           OR caseSubject LIKE '%' || :query || '%' 
        ORDER BY registrationDate DESC
    """)
    fun searchCases(query: String): Flow<List<Case>> // ✅ Search
}
