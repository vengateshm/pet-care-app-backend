package dev.vengateshm.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.vengateshm.db.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(appDatabaseConfig: AppDatabaseConfig) {
        // Connect to database
        Database.connect(hikari(appDatabaseConfig))
        transaction {
            with(SchemaUtils) {
                create(BreedTable)
                create(SpeciesTypeTable)
                create(UserTable)
                create(HospitalTable)
                create(PhysicianTable)
                create(TimeSlotTable)
                create(AppointmentTable)
                create(PetTable)
                create(AppServiceTable)
                create(SpecializationTable)
            }
        }
    }

    private fun hikari(appDatabaseConfig: AppDatabaseConfig): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = appDatabaseConfig.driverClassName
            jdbcUrl = appDatabaseConfig.jdbcUrl
            username = appDatabaseConfig.username
            password = appDatabaseConfig.password
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }
}