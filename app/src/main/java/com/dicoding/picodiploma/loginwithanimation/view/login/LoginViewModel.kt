package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        // Check login status on ViewModel initialization
        checkLoginStatus()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.error == false) { // Explicitly check if login is successful
                    response.loginResult?.let { result ->
                        val name = result.name ?: "Unknown"
                        val token = result.token ?: ""
                        // Save user session and update login status
                        repository.saveSession(UserModel(name, token, isLogin = true))
                        _isLoggedIn.value = true
                    }
                } else {
                    _errorMessage.value = response.message
                    _isLoggedIn.value = false
                }
                _loginResponse.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoggedIn.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                // Collect the flow to get the UserModel object
                val user = repository.getSession().first() // Get the first value from the flow
                _isLoggedIn.value = user.isLogin // Check if the user is logged in
            } catch (e: Exception) {
                _isLoggedIn.value = false // In case of error, set login status to false
            }
        }
    }


}
