package com.sigismund.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    //TODO: Create alternative datasource for deployment

    //CURRENT VERSION ONLY FOR LOCALHOST!! REQUIRE CONNECTED PSQL DATABASE

    fun init() {
        Database.connect(hikariForLocalHosting())

        transaction {
            SchemaUtils.create(RecipesTable)
        }
    }


    private fun hikariForLocalHosting(): HikariDataSource {

        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            jdbcUrl = System.getenv("JDBC_DATABASE_URL")
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    private fun hikariForHostingWithHeroku(): HikariDataSource {

        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            val credentialsAndConnection = System.getenv("DATABASE_URL").split("@")
            val credentials = credentialsAndConnection[0].split("postgres://")[1].split(":")
            val connection = credentialsAndConnection[1]
            driverClassName = System.getenv("JDBC_DRIVER")
            jdbcUrl = "jdbc:postgresql://$connection"
            username = credentials[0]
            password = credentials[1]
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
        block: () -> T
    ): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
