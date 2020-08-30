package com.sigismund.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikariForHostingWithHeroku())

        transaction {
            SchemaUtils.create(Recipes)
        }
    }


    private fun hikariForLocalHosting(): HikariDataSource { //ONLY FOR LOCALHOST!! REQUIRE CONNECTED PSQL DATABASE

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

    private fun hikariForHostingWithHeroku(): HikariDataSource { //ONLY FOR HEROKU POSTGRES DATABASE

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            val credentialsAndConnection = System.getenv("DATABASE_URL").split("@")
            val credentials = credentialsAndConnection[0].split("postgres://")[1].split(":")
            val connection = credentialsAndConnection[1]
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
            transaction {
                addLogger(StdOutSqlLogger)
                block()
            }
        }
}
