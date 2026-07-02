package com.example.serviconectamobile.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconectamobile.data.LoginRequest
import com.example.serviconectamobile.data.RegisterRequest
import com.example.serviconectamobile.data.SessionManager
import com.example.serviconectamobile.network.ApiService
import com.example.serviconectamobile.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService = RetrofitClient.instance
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var selectedRole by mutableStateOf("Cliente")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var loginSuccess by mutableStateOf(false)
    var registerSuccess by mutableStateOf(false)

    fun onLoginClick(context: Context) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // GUARDAR SESIÓN EN PREFERENCIAS
                        val session = SessionManager(context)
                        session.saveSession(user.UserID, user.FirstName, user.UserRole)
                        loginSuccess = true
                    }
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun onRegisterClick() {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = apiService.register(
                    RegisterRequest(email, password, firstName, lastName, selectedRole)
                )
                if (response.isSuccessful) {
                    registerSuccess = true
                } else {
                    errorMessage = "El correo ya existe o los datos son inválidos"
                }
            } catch (e: Exception) {
                errorMessage = "Sin conexión con el servidor"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetStates() {
        loginSuccess = false
        registerSuccess = false
        errorMessage = ""
    }
}