package com.example.translating.component

import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

@Component
class DatabaseConnector {

    fun getConnection(): Connection? {
        val connectionString = "jdbc:sqlserver://buspark.database.windows.net:1433;database=BusParkDB;user=busAdmin@buspark;password=20TarasFake;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
        var connection: Connection? = null
        try {
            connection = DriverManager.getConnection(connectionString)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return connection
    }
}
