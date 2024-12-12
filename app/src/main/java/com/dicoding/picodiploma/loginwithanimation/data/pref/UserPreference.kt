package com.dicoding.picodiploma.loginwithanimation.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    // Keys for DataStore
    private val NAME_KEY = stringPreferencesKey("name")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val IS_LOGIN_KEY = booleanPreferencesKey("is_login")

    // Function to get user session from DataStore
    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                name = preferences[NAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    // Function to get UserModel directly (getting the first value from Flow)
    suspend fun getUserModel(): UserModel {
        return getSession().map { it }.first()
    }

    // Function to save user session to DataStore
    suspend fun saveSession(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userModel.name
            preferences[EMAIL_KEY] = userModel.email
            preferences[TOKEN_KEY] = userModel.token
            preferences[IS_LOGIN_KEY] = userModel.isLogin
        }
    }

    // Function to logout (clear user session)
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[EMAIL_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[IS_LOGIN_KEY] = false
        }
    }

    // Singleton instance
    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
