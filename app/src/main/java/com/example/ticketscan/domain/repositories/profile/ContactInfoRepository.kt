package com.example.ticketscan.domain.repositories.profile

import com.example.ticketscan.domain.model.ContactInfo
import kotlinx.coroutines.flow.Flow

interface ContactInfoRepository {
    val contactInfo: Flow<ContactInfo>

    suspend fun save(contactInfo: ContactInfo)
}
