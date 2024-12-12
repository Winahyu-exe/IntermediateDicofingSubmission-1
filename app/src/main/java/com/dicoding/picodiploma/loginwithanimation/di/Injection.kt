package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService

// Declaring DataStore for preferences
val Context.dataStore by preferencesDataStore(name = "user_preferences")

object Injection {

    // Provide the ApiService instance
    fun provideApiService(token: String): ApiService {
        return ApiConfig.getApiService(token)  // Using the token to initialize ApiService
    }

    // Provide the UserRepository instance
    suspend fun provideRepository(context: Context): UserRepository {
        // Get the instance of UserPreference
        val pref = UserPreference.getInstance(context.dataStore)  // Pass the dataStore to get the instance

        // Get the UserModel asynchronously
        val userModel = pref.getUserModel() // Assuming getUserModel() already handles suspending internally

        // Initialize ApiService with the token from UserModel
        val apiService = provideApiService(userModel.token)

        // Return the UserRepository instance
        return UserRepository.getInstance(pref, apiService)
    }
}
