package dev.vengateshm.routes

import dev.vengateshm.db.tables.AppServiceTable
import dev.vengateshm.models.AppService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.appServiceRoutes() {
    route("api/v1/app-services") {
        post {
            val appService = call.receive<AppService>()
            val id = transaction {
                AppServiceTable.insertAndGetId {
                    it[name] = appService.name
                    it[imgUrl] = appService.imgUrl
                }
            }
            call.respond(HttpStatusCode.Created, id.value)
        }

        get {
            val appServices = transaction {
                AppServiceTable.selectAll().map {
                    AppService(
                        id = it[AppServiceTable.id].value,
                        name = it[AppServiceTable.name],
                        imgUrl = it[AppServiceTable.imgUrl]
                    )
                }
            }
            call.respond(appServices)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val appService = transaction {
                AppServiceTable.select { AppServiceTable.id eq id }
                    .singleOrNull()
                    ?.let {
                        AppService(
                            id = it[AppServiceTable.id].value,
                            name = it[AppServiceTable.name],
                            imgUrl = it[AppServiceTable.imgUrl]
                        )
                    }
            }
            if (appService != null) {
                call.respond(appService)
            } else {
                call.respond(HttpStatusCode.NotFound, "App service not found")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val updatedAppService = call.receive<AppService>()
            val updatedRowCount = transaction {
                AppServiceTable.update({ AppServiceTable.id eq id }) {
                    it[name] = updatedAppService.name
                    it[imgUrl] = updatedAppService.imgUrl
                }
            }
            if (updatedRowCount > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "App service not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            val deletedRowCount = transaction {
                AppServiceTable.deleteWhere { AppServiceTable.id eq id }
            }
            if (deletedRowCount > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "App service not found")
            }
        }

        post("/multiple") {
            val appServices = call.receive<List<AppService>>()
            val insertedIds = transaction {
                appServices.map { appService ->
                    AppServiceTable.insertAndGetId {
                        it[name] = appService.name
                        it[imgUrl] = appService.imgUrl
                    }.value
                }
            }
            call.respond(HttpStatusCode.Created, insertedIds)
        }
    }
}