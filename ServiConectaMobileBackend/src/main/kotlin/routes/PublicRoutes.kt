package routes

import database.DbConnection
import models.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import java.sql.Connection

fun Route.publicRoutes() {
    route("/public") {

        get("/categories") {
            val db = DbConnection.getConnection() ?: return@get call.respond(HttpStatusCode.InternalServerError)
            try {
                val categories = mutableListOf<Category>()
                val rs = db.createStatement().executeQuery("SELECT CategoryID, CategoryName, Description FROM Categories")
                while (rs.next()) {
                    categories.add(Category(rs.getInt("CategoryID"), rs.getString("CategoryName"), rs.getString("Description")))
                }
                call.respond(categories)
            } finally { db.close() }
        }

        get("/contractors/{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val db = DbConnection.getConnection() ?: return@get call.respond(HttpStatusCode.InternalServerError)
            try {
                val contractors = mutableListOf<Contractor>()
                val query = """
                    SELECT U.UserID, U.FirstName, U.LastName, CP.BusinessName, CP.Bio, CP.YearsOfExperience,
                    COALESCE((SELECT AVG(CAST(Rating AS FLOAT)) FROM Reviews WHERE ContractorID = U.UserID), 0.0) as AvgRating
                    FROM Users U
                    JOIN ContractorProfiles CP ON U.UserID = CP.UserID
                    JOIN ContractorCategories CC ON U.UserID = CC.UserID
                    WHERE CC.CategoryID = ?
                """.trimIndent()
                val stmt = db.prepareStatement(query)
                stmt.setInt(1, categoryId)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    val rawRating = rs.getDouble("AvgRating")
                    contractors.add(Contractor(
                        rs.getInt("UserID"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("BusinessName"), rs.getString("Bio"), rs.getInt("YearsOfExperience"),
                        if (rawRating == 0.0) 0.0 else String.format("%.1f", rawRating).toDouble()
                    ))
                }
                call.respond(contractors)
            } finally { db.close() }
        }

        get("/contractor-detail/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val db = DbConnection.getConnection() ?: return@get call.respond(HttpStatusCode.InternalServerError)
            try {
                val pQuery = """
                    SELECT U.FirstName, U.LastName, CP.BusinessName, CP.Bio, CP.YearsOfExperience,
                    COALESCE((SELECT AVG(CAST(Rating AS FLOAT)) FROM Reviews WHERE ContractorID = U.UserID), 0.0) as AvgRating
                    FROM Users U JOIN ContractorProfiles CP ON U.UserID = CP.UserID WHERE U.UserID = ?
                """.trimIndent()
                val pStmt = db.prepareStatement(pQuery)
                pStmt.setInt(1, id)
                val pRs = pStmt.executeQuery()
                if (!pRs.next()) return@get call.respond(HttpStatusCode.NotFound)

                val avgRating = pRs.getDouble("AvgRating")
                val profile = Contractor(
                    id, pRs.getString("FirstName"), pRs.getString("LastName"),
                    pRs.getString("BusinessName"), pRs.getString("Bio"), pRs.getInt("YearsOfExperience"),
                    if (avgRating == 0.0) 0.0 else String.format("%.1f", avgRating).toDouble()
                )

                val reviews = mutableListOf<Review>()
                val rQuery = "SELECT R.ReviewID, R.Rating, R.Comment, U.FirstName + ' ' + U.LastName as ClientName, CONVERT(VARCHAR, R.CreatedAt, 103) as Fecha FROM Reviews R JOIN Users U ON R.ClientID = U.UserID WHERE R.ContractorID = ? ORDER BY R.CreatedAt DESC"
                val rStmt = db.prepareStatement(rQuery)
                rStmt.setInt(1, id)
                val rRs = rStmt.executeQuery()
                while (rRs.next()) {
                    reviews.add(Review(rRs.getInt("ReviewID"), rRs.getInt("Rating"), rRs.getString("Comment"), rRs.getString("ClientName"), rRs.getString("Fecha")))
                }
                call.respond(ContractorDetailResponse(profile, reviews, if (avgRating == 0.0) 0.0 else String.format("%.1f", avgRating).toDouble()))
            } finally { db.close() }
        }

        post("/reviews") {
            val data = try { call.receive<ReviewRequest>() } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
                return@post
            }
            val db = DbConnection.getConnection() ?: return@post call.respond(HttpStatusCode.InternalServerError)
            try {
                val query = "INSERT INTO Reviews (Rating, Comment, ClientID, ContractorID) VALUES (?, ?, ?, ?)"
                val stmt = db.prepareStatement(query)
                stmt.setInt(1, data.Rating)
                stmt.setString(2, data.Comment)
                stmt.setInt(3, data.ClientID)
                stmt.setInt(4, data.ContractorID)
                stmt.executeUpdate()
                call.respond(HttpStatusCode.Created, "Reseña guardada")
            } finally { db.close() }
        }
    }
}