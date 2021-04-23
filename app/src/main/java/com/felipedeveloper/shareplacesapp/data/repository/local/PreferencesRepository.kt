package com.felipedeveloper.shareplacesapp.data.repository.local

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository(context: Context) {

    companion object {

        const val DEFAULT_NAME = "Default"

        val IS_LOGGED = preferencesKey<Boolean>("is_logged")
        val USER_EMAIL = preferencesKey<String>("user_email")
        val UID = preferencesKey<String>("uid")

    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = "Preferences"
    )

    val userEmail: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            preference[USER_EMAIL] ?: DEFAULT_NAME
        }


    val uid: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            preference[UID] ?: DEFAULT_NAME
        }

    val isLogged: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            preference[IS_LOGGED] ?: false
        }


    suspend fun setUid(uid: String) {
        dataStore.edit { preferences ->
            preferences[UID] = uid
        }
    }

    suspend fun setUserEmail(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = name
        }
    }


    suspend fun setLoggedMode(isLogged: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED] = isLogged
        }
    }

    suspend fun clearDataStore() {
        dataStore.edit { it.clear() }
    }
}