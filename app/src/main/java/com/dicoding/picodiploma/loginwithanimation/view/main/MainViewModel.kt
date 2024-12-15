package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
class MainViewModel(
    private val repository: UserRepository,
    private val apiService: ApiService
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    // Get story data from the API using ApiService
    fun getStoryData(): LiveData<StoryResponse> {
        return liveData(Dispatchers.IO) {
            try {
                // Dapatkan session dan token dari repository
                val session = repository.getSession().firstOrNull()
                Log.d("MainViewModel", "Session token: ${session?.token}")

                // Periksa apakah session null atau token kosong
                if (session == null || session.token.isEmpty()) {
                    emit(StoryResponse(error = true, message = "No token available"))
                    Log.e("MainViewModel", "Session is null or token is empty")
                    return@liveData
                }

                // Panggil API langsung dengan token
                val response = apiService.getStories("Bearer ${session.token}")
                emit(response)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching stories: ${e.message}")
                emit(StoryResponse(error = true, message = e.message))
            }
        }
    }


    // Logout function
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

