package com.example.ticketscan.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.data.profile.ContactInfoRepositoryImpl
import com.example.ticketscan.domain.model.ContactInfo
import com.example.ticketscan.domain.repositories.profile.ContactInfoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactInfoViewModel(
    private val repository: ContactInfoRepository
) : ViewModel() {

    val contactInfo: StateFlow<ContactInfo> = repository.contactInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ContactInfo()
        )

    fun save(contactInfo: ContactInfo, onResult: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.save(contactInfo)
            onResult?.invoke()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

class ContactInfoViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val repository: ContactInfoRepository by lazy {
        ContactInfoRepositoryImpl(context.applicationContext)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
