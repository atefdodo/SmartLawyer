package com.smartlawyer.data.repository

import com.smartlawyer.data.dao.ClientDao
import com.smartlawyer.data.entities.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientDao: ClientDao
) {
    fun getAllClients(): Flow<List<Client>> = clientDao.getAllClients()
    
    fun searchClients(searchQuery: String): Flow<List<Client>> = clientDao.searchClients(searchQuery)
    
    suspend fun getClientById(clientId: Long): Client? = clientDao.getClientById(clientId)
    
    suspend fun insertClient(client: Client): Long = clientDao.insertClient(client)
    
    suspend fun updateClient(client: Client) = clientDao.updateClient(client)
    
    suspend fun deleteClient(client: Client) = clientDao.deleteClient(client)
    
    suspend fun getClientCount(): Int = clientDao.getClientCount()
} 