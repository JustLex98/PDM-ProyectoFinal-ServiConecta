package com.example.serviconectamobile.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconectamobile.data.CategoryResponse
import com.example.serviconectamobile.network.RetrofitClient
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    var categories by mutableStateOf<List<CategoryResponse>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = RetrofitClient.instance.getCategories()
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }
}