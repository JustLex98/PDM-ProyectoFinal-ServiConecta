package com.example.serviconectamobile

import com.example.serviconectamobile.data.CategoryResponse
import com.example.serviconectamobile.viewmodel.CatalogViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadCategories_apiRetornaCategorias_actualizaListaCategorias() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.categoriesResult = listOf(
            CategoryResponse(
                CategoryID = 1,
                CategoryName = "Electricidad",
                Description = "Servicios eléctricos"
            ),
            CategoryResponse(
                CategoryID = 2,
                CategoryName = "Plomería",
                Description = "Servicios de agua"
            )
        )

        val viewModel = CatalogViewModel(fakeApi)

        advanceUntilIdle()

        assertEquals(2, viewModel.categories.size)
        assertEquals("Electricidad", viewModel.categories[0].CategoryName)
        assertEquals("Plomería", viewModel.categories[1].CategoryName)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun loadCategories_apiRetornaListaVacia_mantieneListaVacia() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.categoriesResult = emptyList()

        val viewModel = CatalogViewModel(fakeApi)

        advanceUntilIdle()

        assertEquals(0, viewModel.categories.size)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun loadCategories_apiFalla_noRevientaYLoadingQuedaFalse() = runTest {
        val fakeApi = FakeApiService()
        fakeApi.shouldThrowError = true

        val viewModel = CatalogViewModel(fakeApi)

        advanceUntilIdle()

        assertEquals(0, viewModel.categories.size)
        assertFalse(viewModel.isLoading)
    }
}