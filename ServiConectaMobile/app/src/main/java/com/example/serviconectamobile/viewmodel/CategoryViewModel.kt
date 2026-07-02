package com.example.serviconectamobile.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconectamobile.data.CategoryResponse
import com.example.serviconectamobile.network.ApiService
import com.example.serviconectamobile.network.RetrofitClient
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val apiService: ApiService = RetrofitClient.instance
) : ViewModel() {
    var categories by mutableStateOf<List<CategoryResponse>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = apiService.getCategories()
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }
}