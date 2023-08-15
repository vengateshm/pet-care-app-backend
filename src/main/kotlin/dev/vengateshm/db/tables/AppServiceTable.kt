package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object AppServiceTable : IntIdTable("app_service") {
    val name = varchar("name", 255)
    val imgUrl = varchar("img_url", 255)
}