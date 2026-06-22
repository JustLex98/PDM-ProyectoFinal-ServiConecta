package models

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val ReviewID: Int,
    val Rating: Int,
    val Comment: String?,
    val ClientName: String, // Nombre del cliente que calificó
    val CreatedAt: String
)