package com.example.serviconectamobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.serviconectamobile.data.SessionManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SessionManagerTest {

    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sessionManager = SessionManager(context)
        sessionManager.logout()
    }

    @Test
    fun isLoggedIn_sinSesionGuardada_retornaFalse() {
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun saveSession_guardaDatosDelUsuario() {
        sessionManager.saveSession(
            userId = 25,
            firstName = "Miguel",
            userRole = "Cliente"
        )

        assertTrue(sessionManager.isLoggedIn())
        assertEquals(25, sessionManager.getUserId())
        assertEquals("Miguel", sessionManager.getUserName())
        assertEquals("Cliente", sessionManager.getUserRole())
    }

    @Test
    fun logout_borraSesionGuardada() {
        sessionManager.saveSession(
            userId = 30,
            firstName = "Ricardo",
            userRole = "Contratista"
        )

        sessionManager.logout()

        assertFalse(sessionManager.isLoggedIn())
        assertEquals(-1, sessionManager.getUserId())
        assertEquals("Usuario", sessionManager.getUserName())
        assertEquals("Cliente", sessionManager.getUserRole())
    }
}