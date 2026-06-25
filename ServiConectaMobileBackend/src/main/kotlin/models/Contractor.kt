package models

import kotlinx.serialization.Serializable

@Serializable
data class Contractor(
    val UserID: Int,
    val FirstName: String,
    val LastName: String,
    val BusinessName: String?,
    val Bio: String?,
    val YearsOfExperience: Int,
    val AvrRating: Double
)

@Serializable
data class ContractorDetailResponse(
    val profile: Contractor,
    val reviews: List<Review>,
    val averageRating: Double
)