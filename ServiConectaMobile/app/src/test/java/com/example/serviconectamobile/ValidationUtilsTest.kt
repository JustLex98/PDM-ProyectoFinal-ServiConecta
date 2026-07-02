package com.example.serviconectamobile

import com.example.serviconectamobile.utils.ValidationUtils
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidationUtilsTest {

    @Test
    fun validateLogin_camposVacios_retornaMensajeError() {
        val result = ValidationUtils.validateLogin("", "")

        assertEquals("Completa todos los campos", result)
    }

    @Test
    fun validateLogin_correoInvalido_retornaMensajeError() {
        val result = ValidationUtils.validateLogin("correo-malo", "123456")

        assertEquals("Correo inválido", result)
    }

    @Test
    fun validateLogin_passwordCorta_retornaMensajeError() {
        val result = ValidationUtils.validateLogin("cliente@test.com", "123")

        assertEquals("La contraseña debe tener al menos 6 caracteres", result)
    }

    @Test
    fun validateLogin_datosCorrectos_retornaNull() {
        val result = ValidationUtils.validateLogin("cliente@test.com", "123456")

        assertNull(result)
    }

    @Test
    fun validateRegister_camposVacios_retornaMensajeError() {
        val result = ValidationUtils.validateRegister("", "", "", "")

        assertEquals("Todos los campos son obligatorios", result)
    }

    @Test
    fun validateRegister_correoInvalido_retornaMensajeError() {
        val result = ValidationUtils.validateRegister(
            email = "correo-malo",
            password = "123456",
            firstName = "Mario",
            lastName = "Molina"
        )

        assertEquals("Correo inválido", result)
    }

    @Test
    fun validateRegister_datosCorrectos_retornaNull() {
        val result = ValidationUtils.validateRegister(
            email = "nuevo@test.com",
            password = "123456",
            firstName = "Mario",
            lastName = "Molina"
        )

        assertNull(result)
    }

    @Test
    fun isValidRating_ratingEntreUnoYCinco_retornaTrue() {
        assertTrue(ValidationUtils.isValidRating(1))
        assertTrue(ValidationUtils.isValidRating(3))
        assertTrue(ValidationUtils.isValidRating(5))
    }

    @Test
    fun isValidRating_ratingFueraDeRango_retornaFalse() {
        assertFalse(ValidationUtils.isValidRating(0))
        assertFalse(ValidationUtils.isValidRating(6))
    }

    @Test
    fun isValidExperience_experienciaValida_retornaTrue() {
        assertTrue(ValidationUtils.isValidExperience(0))
        assertTrue(ValidationUtils.isValidExperience(10))
        assertTrue(ValidationUtils.isValidExperience(60))
    }

    @Test
    fun isValidExperience_experienciaInvalida_retornaFalse() {
        assertFalse(ValidationUtils.isValidExperience(-1))
        assertFalse(ValidationUtils.isValidExperience(61))
    }

    @Test
    fun buildChatId_idsEnCualquierOrden_retornaSiempreMismoId() {
        val chatId1 = ValidationUtils.buildChatId(10, 3)
        val chatId2 = ValidationUtils.buildChatId(3, 10)

        assertEquals("3_10", chatId1)
        assertEquals(chatId1, chatId2)
    }

    @Test
    fun buildChatId_idInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException::class.java) {
            ValidationUtils.buildChatId(0, 5)
        }
    }
}