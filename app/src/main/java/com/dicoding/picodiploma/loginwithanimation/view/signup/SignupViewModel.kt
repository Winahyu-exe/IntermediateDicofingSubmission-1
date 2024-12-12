package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel : ViewModel() {
    private val _isPasswordValid = MutableLiveData<Boolean>(false)
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _registerResponse = MutableLiveData<RegisterResponse?>()
    val registerResponse: LiveData<RegisterResponse?> get() = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Fungsi validasi input
    private fun validateInput(name: String, email: String, password: String): Boolean {
        when {
            name.isBlank() -> {
                _errorMessage.value = "Nama tidak boleh kosong"
                return false
            }
            email.isBlank() -> {
                _errorMessage.value = "Email tidak boleh kosong"
                return false
            }
            password.isBlank() -> {
                _errorMessage.value = "Password tidak boleh kosong"
                return false
            }
            password.length < 8 -> {
                _errorMessage.value = "Password harus memiliki minimal 8 karakter"
                return false
            }
            else -> {
                _errorMessage.value = null // Semua validasi lolos
                return true
            }
        }
    }

    // Fungsi registrasi pengguna
    fun registerUser(name: String, email: String, password: String) {
        if (!validateInput(name, email, password)) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().register(name, email, password)
                if (response.error == true) {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan"
                } else {
                    _registerResponse.value = response
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    _errorMessage.value = errorResponse.message
                } else {
                    _errorMessage.value = "Terjadi kesalahan jaringan"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan tidak diketahui"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
