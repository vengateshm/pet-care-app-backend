package dev.vengateshm.db.ext

import dev.vengateshm.db.tables.UserTable
import dev.vengateshm.models.User
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow?.toUser(): User? {
    if (this == null) return null
    return User(
        id = this[UserTable.id].value,
        name = this[UserTable.name],
        email = this[UserTable.email],
        password = this[UserTable.password],
        salt = this[UserTable.salt]
    )
}