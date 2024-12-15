package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    // Menyimpan sesi pengguna ke dalam DataStore
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    // Mendapatkan sesi pengguna dari DataStore
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    // Melakukan login ke API dan menyimpan token jika berhasil
    suspend fun login(email: String, password: String): LoginResponse {
        // Call the API with the provided email and password
        val response = apiService.login(mapOf("email" to email, "password" to password))

        // Check if the response indicates success and process loginResult
        if (response.error == false) {
            response.loginResult?.let {
                // Safely handle nullable fields and save session
                val name = it.name ?: "Unknown" // Provide default values for null fields
                val token = it.token ?: ""
                saveSession(UserModel(name, token)) // Save session using UserModel
            }
        }

        // Return the complete response to allow the caller to handle messages or errors
        return response
    }

    // Mengambil daftar cerita menggunakan token dari sesi pengguna
    suspend fun getStories(token: String): StoryResponse {
        return apiService.getStories("Bearer $token")
    }

    // Menghapus sesi pengguna dari DataStore
    suspend fun logout() {
        userPreference.logout()
    }

    // Method to get the user token from UserPreference
    suspend fun getUserToken(): String {
        val userModel = userPreference.getSession().firstOrNull() ?: throw IllegalStateException("No user session available")
        return userModel.token ?: throw IllegalStateException("Token is null")
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        // Implementasi Singleton
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
