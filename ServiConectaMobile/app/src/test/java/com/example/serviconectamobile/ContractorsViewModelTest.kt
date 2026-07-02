package com.example.serviconectamobile

import com.example.serviconectamobile.data.ContractorResponse
import com.example.serviconectamobile.viewmodel.ContractorsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContractorsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadContractors_apiRetornaContratistas_actualizaListaContratistas() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.contractorsResult = listOf(
            ContractorResponse(
                UserID = 10,
                FirstName = "Luis",
                LastName = "Ramírez",
                BusinessName = "Electricidad LR",
                Bio = "Técnico electricista",
                YearsOfExperience = 5,
                AvrRating = 4.8
            ),
            ContractorResponse(
                UserID = 11,
                FirstName = "Carlos",
                LastName = "Pérez",
                BusinessName = "Servicios CP",
                Bio = "Contratista general",
                YearsOfExperience = 3,
                AvrRating = 4.2
            )
        )

        val viewModel = ContractorsViewModel(fakeApi)

        viewModel.loadContractors(categoryId = 1)
        advanceUntilIdle()

        assertEquals(2, viewModel.contractors.size)
        assertEquals("Luis", viewModel.contractors[0].FirstName)
        assertEquals("Electricidad LR", viewModel.contractors[0].BusinessName)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun loadContractors_apiRetornaListaVacia_mantieneListaVacia() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.contractorsResult = emptyList()

        val viewModel = ContractorsViewModel(fakeApi)

        viewModel.loadContractors(categoryId = 1)
        advanceUntilIdle()

        assertEquals(0, viewModel.contractors.size)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun loadContractors_apiFalla_dejaListaVaciaYLoadingFalse() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.shouldThrowError = true

        val viewModel = ContractorsViewModel(fakeApi)

        viewModel.loadContractors(categoryId = 1)
        advanceUntilIdle()

        assertEquals(0, viewModel.contractors.size)
        assertFalse(viewModel.isLoading)
    }
}