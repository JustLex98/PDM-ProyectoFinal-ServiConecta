package com.example.serviconectamobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.serviconectamobile.data.SessionManager
import com.example.serviconectamobile.data.UserResponse
import com.example.serviconectamobile.viewmodel.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sessionManager = SessionManager(context)
        sessionManager.logout()
    }

    @Test
    fun onLoginClick_camposVacios_muestraMensajeDeError() {
        val fakeApi = FakeApiService()
        val viewModel = LoginViewModel(fakeApi)

        viewModel.email = ""
        viewModel.password = ""

        viewModel.onLoginClick(context)

        assertEquals("Completa todos los campos", viewModel.errorMessage)
        assertFalse(viewModel.loginSuccess)
    }

    @Test
    fun onLoginClick_credencialesCorrectas_guardaSesionYLoginSuccessTrue() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.loginResponse = Response.success(
            UserResponse(
                UserID = 99,
                Email = "cliente@test.com",
                FirstName = "Miguel",
                LastName = "Demo",
                UserRole = "Cliente"
            )
        )

        val viewModel = LoginViewModel(fakeApi)
        viewModel.email = "cliente@test.com"
        viewModel.password = "123456"

        viewModel.onLoginClick(context)
        advanceUntilIdle()

        assertTrue(viewModel.loginSuccess)
        assertEquals("", viewModel.errorMessage)
        assertEquals(99, sessionManager.getUserId())
        assertEquals("Miguel", sessionManager.getUserName())
        assertEquals("Cliente", sessionManager.getUserRole())
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun onLoginClick_credencialesIncorrectas_muestraMensajeError() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.loginResponse = Response.error(
            401,
            okhttp3.ResponseBody.create(null, "Unauthorized")
        )

        val viewModel = LoginViewModel(fakeApi)
        viewModel.email = "malo@test.com"
        viewModel.password = "123456"

        viewModel.onLoginClick(context)
        advanceUntilIdle()

        assertFalse(viewModel.loginSuccess)
        assertEquals("Correo o contraseña incorrectos", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun onLoginClick_errorConexion_muestraMensajeErrorConexion() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.shouldThrowError = true

        val viewModel = LoginViewModel(fakeApi)
        viewModel.email = "cliente@test.com"
        viewModel.password = "123456"

        viewModel.onLoginClick(context)
        advanceUntilIdle()

        assertFalse(viewModel.loginSuccess)
        assertTrue(viewModel.errorMessage.startsWith("Error de conexión"))
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun onRegisterClick_camposVacios_muestraMensajeError() {
        val fakeApi = FakeApiService()
        val viewModel = LoginViewModel(fakeApi)

        viewModel.email = ""
        viewModel.password = ""
        viewModel.firstName = ""
        viewModel.lastName = ""

        viewModel.onRegisterClick()

        assertEquals("Todos los campos son obligatorios", viewModel.errorMessage)
        assertFalse(viewModel.registerSuccess)
    }

    @Test
    fun onRegisterClick_datosCorrectos_registerSuccessTrue() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.registerResponse = Response.success(Unit)

        val viewModel = LoginViewModel(fakeApi)
        viewModel.email = "nuevo@test.com"
        viewModel.password = "123456"
        viewModel.firstName = "Nuevo"
        viewModel.lastName = "Usuario"
        viewModel.selectedRole = "Cliente"

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertTrue(viewModel.registerSuccess)
        assertEquals("", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun onRegisterClick_apiFalla_muestraMensajeError() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.shouldThrowError = true

        val viewModel = LoginViewModel(fakeApi)
        viewModel.email = "nuevo@test.com"
        viewModel.password = "123456"
        viewModel.firstName = "Nuevo"
        viewModel.lastName = "Usuario"

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertFalse(viewModel.registerSuccess)
        assertEquals("Sin conexión con el servidor", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun resetStates_limpiaMensajesYEstados() {
        val fakeApi = FakeApiService()
        val viewModel = LoginViewModel(fakeApi)

        viewModel.errorMessage = "Error previo"
        viewModel.loginSuccess = true
        viewModel.registerSuccess = true

        viewModel.resetStates()

        assertEquals("", viewModel.errorMessage)
        assertFalse(viewModel.loginSuccess)
        assertFalse(viewModel.registerSuccess)
    }
}