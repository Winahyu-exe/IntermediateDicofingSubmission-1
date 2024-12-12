package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
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
            // Observe session data
            val session = repository.getSession().asLiveData().value
            if (session?.token.isNullOrEmpty()) {
                // Handle the case where the token is unavailable
                emit(StoryResponse(error = true, message = "No token available"))
                return@liveData
            }

            // If token is available, fetch stories
            val response = apiService.getStories("Bearer ${session?.token}")
            emit(response)
        }
    }


    // Logout function
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
