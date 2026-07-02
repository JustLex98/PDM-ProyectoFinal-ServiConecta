package com.example.serviconectamobile

import com.example.serviconectamobile.data.CategoryResponse
import com.example.serviconectamobile.data.ContractorFullDetail
import com.example.serviconectamobile.data.ContractorResponse
import com.example.serviconectamobile.data.LoginRequest
import com.example.serviconectamobile.data.RegisterRequest
import com.example.serviconectamobile.data.ReviewRequest
import com.example.serviconectamobile.data.UpdateContractorRequest
import com.example.serviconectamobile.data.UserResponse
import com.example.serviconectamobile.network.ApiService
import retrofit2.Response

class FakeApiService : ApiService {

    var shouldThrowError: Boolean = false

    var loginResponse: Response<UserResponse> = Response.success(
        UserResponse(
            UserID = 1,
            Email = "cliente@test.com",
            FirstName = "Cliente",
            LastName = "Demo",
            UserRole = "Cliente"
        )
    )

    var registerResponse: Response<Unit> = Response.success(Unit)

    var categoriesResult: List<CategoryResponse> = emptyList()

    var contractorsResult: List<ContractorResponse> = emptyList()

    override suspend fun login(request: LoginRequest): Response<UserResponse> {
        if (shouldThrowError) throw RuntimeException("Error simulado")
        return loginResponse
    }

    override suspend fun register(request: RegisterRequest): Response<Unit> {
        if (shouldThrowError) throw RuntimeException("Error simulado")
        return registerResponse
    }

    override suspend fun updateContractorProfile(request: UpdateContractorRequest): Response<Unit> {
        return Response.success(Unit)
    }

    override suspend fun getCategories(): List<CategoryResponse> {
        if (shouldThrowError) throw RuntimeException("Error simulado")
        return categoriesResult
    }

    override suspend fun getContractors(categoryId: Int): List<ContractorResponse> {
        if (shouldThrowError) throw RuntimeException("Error simulado")
        return contractorsResult
    }

    override suspend fun getContractorDetail(id: Int): ContractorFullDetail {
        throw NotImplementedError("No se usa en estas pruebas")
    }

    override suspend fun postReview(request: ReviewRequest): Response<Unit> {
        return Response.success(Unit)
    }
}