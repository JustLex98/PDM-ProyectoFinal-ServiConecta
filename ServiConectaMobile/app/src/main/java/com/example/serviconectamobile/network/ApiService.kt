package com.example.serviconectamobile.network

import com.example.serviconectamobile.data.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("auth/update-contractor")
    suspend fun updateContractorProfile(@Body request: UpdateContractorRequest): Response<Unit>

    @GET("public/categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("public/contractors/{categoryId}")
    suspend fun getContractors(@Path("categoryId") categoryId: Int): List<ContractorResponse>

    @GET("public/contractor-detail/{id}")
    suspend fun getContractorDetail(@Path("id") id: Int): ContractorFullDetail

    @POST("public/reviews")
    suspend fun postReview(@Body request: ReviewRequest): Response<Unit>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.40.7.89:8080/"
    val instance: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}