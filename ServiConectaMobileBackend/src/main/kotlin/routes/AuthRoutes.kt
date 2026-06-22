package routes

import database.DbConnection
import models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt
import java.sql.Statement

fun Route.authRouting() {
    route("/auth") {

        post("/login") {
            val loginData = try { call.receive<LoginRequest>() } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
                return@post
            }
            val db = DbConnection.getConnection() ?: return@post call.respond(HttpStatusCode.InternalServerError)
            try {
                val query = "SELECT UserID, Email, PasswordHash, FirstName, LastName, UserRole FROM Users WHERE Email = ?"
                val statement = db.prepareStatement(query)
                statement.setString(1, loginData.Email)
                val rs = statement.executeQuery()
                if (rs.next()) {
                    val storedHash = rs.getString("PasswordHash")
                    val isCorrect = if (storedHash.startsWith("$2")) BCrypt.checkpw(loginData.Password, storedHash) else storedHash == loginData.Password
                    if (isCorrect) {
                        call.respond(HttpStatusCode.OK, Usuario(rs.getInt("UserID"), rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"), rs.getString("UserRole")))
                    } else { call.respond(HttpStatusCode.Unauthorized, "Password incorrecta") }
                } else { call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado") }
            } finally { db.close() }
        }

        post("/register") {
            val regData = try { call.receive<RegisterRequest>() } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
                return@post
            }
            val db = DbConnection.getConnection() ?: return@post call.respond(HttpStatusCode.InternalServerError)
            try {
                db.autoCommit = false
                val insUser = db.prepareStatement("INSERT INTO Users (Email, PasswordHash, FirstName, LastName, UserRole) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
                insUser.setString(1, regData.Email)
                insUser.setString(2, BCrypt.hashpw(regData.Password, BCrypt.gensalt()))
                insUser.setString(3, regData.FirstName)
                insUser.setString(4, regData.LastName)
                insUser.setString(5, regData.UserRole)
                insUser.executeUpdate()
                val keys = insUser.generatedKeys
                if (keys.next() && regData.UserRole == "Contratista") {
                    val profileStmt = db.prepareStatement("INSERT INTO ContractorProfiles (UserID, BusinessName, YearsOfExperience) VALUES (?, ?, 0)")
                    profileStmt.setInt(1, keys.getInt(1))
                    profileStmt.setString(2, "${regData.FirstName} Services")
                    profileStmt.executeUpdate()
                }
                db.commit()
                call.respond(HttpStatusCode.Created, "Registrado")
            } catch (e: Exception) { db.rollback(); call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error") }
            finally { db.close() }
        }

        // --- NUEVO: ACTUALIZAR PERFIL PROFESIONAL ---
        post("/update-contractor") {
            val data = call.receive<UpdateContractorRequest>()
            val db = DbConnection.getConnection() ?: return@post call.respond(HttpStatusCode.InternalServerError)
            try {
                db.autoCommit = false
                // 1. Actualizar Perfil
                val upProfile = db.prepareStatement("UPDATE ContractorProfiles SET BusinessName = ?, Bio = ?, YearsOfExperience = ? WHERE UserID = ?")
                upProfile.setString(1, data.BusinessName)
                upProfile.setString(2, data.Bio)
                upProfile.setInt(3, data.YearsOfExperience)
                upProfile.setInt(4, data.UserID)
                upProfile.executeUpdate()

                // 2. Vincular a Categoría (Borrar anterior e insertar nueva)
                val delCat = db.prepareStatement("DELETE FROM ContractorCategories WHERE UserID = ?")
                delCat.setInt(1, data.UserID)
                delCat.executeUpdate()

                val insCat = db.prepareStatement("INSERT INTO ContractorCategories (UserID, CategoryID) VALUES (?, ?)")
                insCat.setInt(1, data.UserID)
                insCat.setInt(2, data.CategoryID)
                insCat.executeUpdate()

                db.commit()
                call.respond(HttpStatusCode.OK, "Perfil actualizado")
            } catch (e: Exception) { db.rollback(); call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error") }
            finally { db.close() }
        }
    }
}