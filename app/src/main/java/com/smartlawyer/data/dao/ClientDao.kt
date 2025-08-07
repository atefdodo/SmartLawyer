package com.smartlawyer.data.dao

import androidx.room.*
import com.smartlawyer.data.entities.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>
    
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: Long): Client?
    
    @Query("SELECT * FROM clients WHERE name LIKE '%' || :searchQuery || '%' OR phoneNumber LIKE '%' || :searchQuery || '%'")
    fun searchClients(searchQuery: String): Flow<List<Client>>
    
    @Insert
    suspend fun insertClient(client: Client): Long
    
    @Update
    suspend fun updateClient(client: Client)
    
    @Delete
    suspend fun deleteClient(client: Client)
    
    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int
} 