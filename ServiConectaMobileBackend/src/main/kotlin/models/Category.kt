package models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val CategoryID: Int,
    val CategoryName: String,
    val Description: String?
)