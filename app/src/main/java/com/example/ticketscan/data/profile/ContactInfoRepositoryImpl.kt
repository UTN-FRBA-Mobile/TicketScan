package com.example.ticketscan.data.profile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ticketscan.domain.model.ContactInfo
import com.example.ticketscan.domain.repositories.profile.ContactInfoRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.contactInfoDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "contact_info"
)

class ContactInfoRepositoryImpl(
    context: Context
) : ContactInfoRepository {

    private val dataStore: DataStore<Preferences> = context.applicationContext.contactInfoDataStore

    override val contactInfo: Flow<ContactInfo> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                ContactInfo(
                    name = prefs[NAME_KEY] ?: DEFAULT.name,
                    lastName = prefs[LAST_NAME_KEY] ?: DEFAULT.lastName,
                    email = prefs[EMAIL_KEY] ?: DEFAULT.email,
                    phone = prefs[PHONE_KEY] ?: DEFAULT.phone
                )
            }

    override suspend fun save(contactInfo: ContactInfo) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = contactInfo.name
            preferences[LAST_NAME_KEY] = contactInfo.lastName
            preferences[EMAIL_KEY] = contactInfo.email
            preferences[PHONE_KEY] = contactInfo.phone
        }
    }

    private companion object {
        val DEFAULT = ContactInfo()
        val NAME_KEY = stringPreferencesKey("name")
        val LAST_NAME_KEY = stringPreferencesKey("last_name")
        val EMAIL_KEY = stringPreferencesKey("email")
        val PHONE_KEY = stringPreferencesKey("phone")
    }
}
