package com.example.serviconectamobile.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ServiConectaPrefs", Context.MODE_PRIVATE)

    // Guarda los datos tras el Login exitoso
    fun saveSession(userId: Int, firstName: String, userRole: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("USER_ID", userId)
        editor.putString("USER_NAME", firstName)
        editor.putString("USER_ROLE", userRole)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    // Devuelve si hay alguien logueado
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    // Devuelve el Nombre (el que pedía ProfileScreen)
    fun getUserName(): String {
        return sharedPreferences.getString("USER_NAME", "Usuario") ?: "Usuario"
    }

    // Devuelve el Rol (el que pedía ProfileScreen)
    fun getUserRole(): String {
        return sharedPreferences.getString("USER_ROLE", "Cliente") ?: "Cliente"
    }

    // Devuelve el ID (el que pedía ProfileScreen)
    fun getUserId(): Int {
        return sharedPreferences.getInt("USER_ID", -1)
    }

    // Borra todo al cerrar sesión
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}