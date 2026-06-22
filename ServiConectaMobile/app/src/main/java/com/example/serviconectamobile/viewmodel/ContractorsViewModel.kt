package com.example.serviconectamobile.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconectamobile.data.ContractorResponse
import com.example.serviconectamobile.network.RetrofitClient
import kotlinx.coroutines.launch

class ContractorsViewModel : ViewModel() {
    var contractors by mutableStateOf<List<ContractorResponse>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadContractors(categoryId: Int) {
        viewModelScope.launch {
            isLoading = true
            contractors = try {
                RetrofitClient.instance.getContractors(categoryId)
            } catch (_: Exception) {
                emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}