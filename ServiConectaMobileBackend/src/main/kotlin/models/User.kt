package models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val UserID: Int,
    val Email: String,
    val FirstName: String,
    val LastName: String,
    val UserRole: String
)

@Serializable
data class LoginRequest(
    val Email: String,
    val Password: String
)

@Serializable
data class RegisterRequest(
    val Email: String,
    val Password: String,
    val FirstName: String,
    val LastName: String,
    val UserRole: String
)

@Serializable
data class UpdateContractorRequest(
    val UserID: Int,
    val BusinessName: String,
    val Bio: String,
    val YearsOfExperience: Int,
    val CategoryID: Int
)

@Serializable
data class ReviewRequest(
    val Rating: Int,
    val Comment: String,
    val ClientID: Int,
    val ContractorID: Int
)