package com.example.serviconectamobile.utils

object ValidationUtils {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isValidEmail(email: String): Boolean {
        return email.trim().matches(emailRegex)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateLogin(email: String, password: String): String? {
        return when {
            email.isBlank() || password.isBlank() -> "Completa todos los campos"
            !isValidEmail(email) -> "Correo inválido"
            !isValidPassword(password) -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    fun validateRegister(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): String? {
        return when {
            email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank() ->
                "Todos los campos son obligatorios"

            !isValidEmail(email) ->
                "Correo inválido"

            !isValidPassword(password) ->
                "La contraseña debe tener al menos 6 caracteres"

            firstName.trim().length < 2 ->
                "El nombre debe tener al menos 2 caracteres"

            lastName.trim().length < 2 ->
                "El apellido debe tener al menos 2 caracteres"

            else -> null
        }
    }

    fun isValidRating(rating: Int): Boolean {
        return rating in 1..5
    }

    fun isValidExperience(years: Int): Boolean {
        return years in 0..60
    }

    fun buildChatId(firstUserId: Int, secondUserId: Int): String {
        require(firstUserId > 0 && secondUserId > 0) {
            "Los IDs de usuario deben ser mayores que cero"
        }

        return listOf(firstUserId, secondUserId)
            .sorted()
            .joinToString("_")
    }
}
