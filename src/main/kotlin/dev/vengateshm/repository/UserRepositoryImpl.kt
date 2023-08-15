package dev.vengateshm.repository

import dev.vengateshm.db.tables.UserTable
import dev.vengateshm.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {
    override fun createUser(user: User): User {
        return transaction {
            val userId = UserTable.insertAndGetId {
                it[name] = user.name
                it[email] = user.email
                it[password] = user.password
                it[salt] = user.salt
            }
            User(userId.value, user.name, user.email, "", "")
        }
    }

    override fun getUserByEmail(email: String): User? {
        return transaction {
            UserTable.select { UserTable.email eq email }
                .mapNotNull { rowToUser(it) }
                .singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            row[UserTable.id].value,
            row[UserTable.name],
            row[UserTable.email],
            row[UserTable.password],
            row[UserTable.salt]
        )
    }
}