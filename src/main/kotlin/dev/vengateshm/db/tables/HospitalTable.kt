package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object HospitalTable : IntIdTable("hospital") {
    val name = varchar("name", 255)
    val location = varchar("location", 255)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val email = varchar("email", 255)
    val phone = varchar("phone", 20)
    val website = varchar("website", 255)
}