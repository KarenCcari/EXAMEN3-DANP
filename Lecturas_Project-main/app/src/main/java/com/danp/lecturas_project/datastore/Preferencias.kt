package com.danp.lecturas_project.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preferencias(
    private val context: Context
) {
    suspend fun saveEstadoSesion(noteEstado: Boolean){
        context.dataStore.edit { preferences ->
            preferences[ESTADO_SESION] = noteEstado
        }
    }

    suspend fun saveNombre(noteNombre: String){
        context.dataStore.edit { preferences ->
            preferences[NOMBRE] = noteNombre
        }
    }
    suspend fun saveEmail(noteEmail: String){
        context.dataStore.edit { preferences ->
            preferences[EMAIL] = noteEmail
        }
    }

    suspend fun saveSkipSesion(noteSkip: Boolean){
        context.dataStore.edit { preferences ->
            preferences[SKIP_SESION] = noteSkip
        }
    }

    val getEstadoSesion: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ESTADO_SESION] ?: false
        }

    val getSkipSesion: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SKIP_SESION] ?: false
        }

    val getNombre: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[NOMBRE] ?: ""
        }
    val getEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL] ?: ""
        }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userPreferences")
        private val ESTADO_SESION = booleanPreferencesKey("key_app_estado_sesion")
        private val SKIP_SESION = booleanPreferencesKey("key_app_skip_sesion")
        private val NOMBRE = stringPreferencesKey("key_app_nombre")
        private val EMAIL = stringPreferencesKey("key_app_email")
    }
}