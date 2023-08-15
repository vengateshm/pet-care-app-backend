package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object PhysicianTable : IntIdTable("physician") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val phone = varchar("phone", 20)
    val hospitalId = integer("hospital_id").references(HospitalTable.id).nullable()
    val specializationId = integer("specialization_id").references(SpecializationTable.id).nullable()
}