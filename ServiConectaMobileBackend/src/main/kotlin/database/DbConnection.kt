package database

import java.sql.Connection
import java.sql.DriverManager

object DbConnection {
    // Reemplaza con tus datos de SQL Server
    private const val URL = "jdbc:sqlserver://localhost:60133;databaseName=ServiConectaDB;encrypt=true;trustServerCertificate=true;"
    private const val USER = "sa"
    private const val PASS = "CodexAstartesSucks"

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASS)
        } catch (e: Exception) {
            println("ERROR DB: ${e.message}")
            null
        }
    }
}