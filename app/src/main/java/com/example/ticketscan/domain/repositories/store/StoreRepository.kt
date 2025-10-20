package com.example.ticketscan.domain.repositories.store

import com.example.ticketscan.domain.model.Store
import java.util.UUID

interface StoreRepository {
    suspend fun getAllStores(): List<Store>
    suspend fun getStoreById(id: UUID): Store?
    suspend fun insertStore(store: Store): Boolean
    suspend fun updateStore(store: Store): Boolean
    suspend fun deleteStore(id: UUID): Boolean
    suspend fun searchStoresByName(query: String, limit: Int = 5): List<Store>
}
