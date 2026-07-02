package com.example.serviconectamobile

import com.example.serviconectamobile.data.CategoryResponse
import com.example.serviconectamobile.data.ContractorResponse
import com.example.serviconectamobile.data.InboxChat
import com.example.serviconectamobile.data.Message
import com.example.serviconectamobile.data.ReviewRequest
import com.example.serviconectamobile.data.UserResponse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelsTest {

    @Test
    fun userResponse_creaUsuarioCorrectamente() {
        val user = UserResponse(
            UserID = 1,
            Email = "cliente@test.com",
            FirstName = "Carlos",
            LastName = "Vasquez",
            UserRole = "Cliente"
        )

        assertEquals(1, user.UserID)
        assertEquals("cliente@test.com", user.Email)
        assertEquals("Carlos", user.FirstName)
        assertEquals("Cliente", user.UserRole)
    }

    @Test
    fun categoryResponse_permiteDescripcionNula() {
        val category = CategoryResponse(
            CategoryID = 2,
            CategoryName = "Electricidad",
            Description = null
        )

        assertEquals(2, category.CategoryID)
        assertEquals("Electricidad", category.CategoryName)
        assertNull(category.Description)
    }

    @Test
    fun contractorResponse_permiteCamposOpcionalesNulos() {
        val contractor = ContractorResponse(
            UserID = 5,
            FirstName = "Luis",
            LastName = "Ramirez",
            BusinessName = null,
            Bio = null,
            YearsOfExperience = 4,
            AvrRating = 4.5
        )

        assertEquals(5, contractor.UserID)
        assertEquals("Luis", contractor.FirstName)
        assertNull(contractor.BusinessName)
        assertEquals(4.5, contractor.AvrRating, 0.0)
    }

    @Test
    fun reviewRequest_guardaDatosDeResena() {
        val review = ReviewRequest(
            Rating = 5,
            Comment = "Excelente servicio",
            ClientID = 1,
            ContractorID = 9
        )

        assertEquals(5, review.Rating)
        assertEquals("Excelente servicio", review.Comment)
        assertEquals(1, review.ClientID)
        assertEquals(9, review.ContractorID)
    }

    @Test
    fun message_valoresPorDefecto_sonCorrectos() {
        val message = Message()

        assertEquals(0, message.senderId)
        assertEquals(0, message.receiverId)
        assertEquals("", message.message)
        assertEquals(0L, message.timestamp)
        assertEquals("", message.senderName)
    }

    @Test
    fun inboxChat_creaResumenDeChatCorrectamente() {
        val chat = InboxChat(
            otherUserId = 12,
            otherUserName = "Técnico Demo",
            lastMessage = "Nos vemos mañana",
            chatId = "1_12"
        )

        assertEquals(12, chat.otherUserId)
        assertEquals("Técnico Demo", chat.otherUserName)
        assertEquals("Nos vemos mañana", chat.lastMessage)
        assertEquals("1_12", chat.chatId)
    }
}