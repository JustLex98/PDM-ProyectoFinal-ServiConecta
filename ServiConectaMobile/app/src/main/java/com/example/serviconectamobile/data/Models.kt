package com.example.serviconectamobile.data

data class UserResponse(
    val UserID: Int,
    val Email: String,
    val FirstName: String,
    val LastName: String,
    val UserRole: String
)
data class LoginRequest(
    val Email: String,
    val Password: String
)
data class RegisterRequest(
    val Email: String,
    val Password: String,
    val FirstName: String,
    val LastName: String,
    val UserRole: String
)
data class UpdateContractorRequest(
    val UserID: Int,
    val BusinessName: String,
    val Bio: String,
    val YearsOfExperience: Int,
    val CategoryID: Int
)

data class CategoryResponse(
    val CategoryID: Int,
    val CategoryName: String,
    val Description: String?
)
data class ContractorResponse(
    val UserID: Int,
    val FirstName: String,
    val LastName: String,
    val BusinessName: String?,
    val Bio: String?,
    val YearsOfExperience: Int,
    val AvrRating: Double
)
data class ReviewResponse(
    val ReviewID: Int,
    val Rating: Int,
    val Comment: String?,
    val ClientName: String,
    val CreatedAt: String
)

data class ContractorFullDetail(
    val profile: ContractorResponse,
    val reviews: List<ReviewResponse>,
    val averageRating: Double
)


data class ReviewRequest(
    val Rating: Int,
    val Comment: String,
    val ClientID: Int,
    val ContractorID: Int
)

data class Message(
    val senderId: Int = 0,
    val receiverId: Int = 0,
    val message: String = "",
    val timestamp: Long = 0L,
    val senderName: String = ""
)

data class InboxChat(
    val otherUserId: Int,
    val otherUserName: String,
    val lastMessage: String,
    val chatId: String
)