/*
 * Choretinder
 * Copyright (C) 2024  Jacob Jerrell (@jjerrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package app.jjerrell.choretender.service.route

import app.jjerrell.choretender.service.domain.IChoreServiceUserRepository
import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import org.koin.ktor.ext.inject

internal fun Routing.userRoutes() {
    val userRepository by inject<IChoreServiceUserRepository>()

    route("user") {
        get("{id?}") {
            call.parameters["id"]?.toLongOrNull()?.let {
                call.application.environment.log.debug("Looking for user with ID: $it")
                val userLookup = userRepository.getUserDetail(it)
                if (userLookup == null) {
                    call.application.environment.log.debug("Failed to locate user with ID: $it")
                    call.respond(
                        HttpStatusCode.NotFound,
                        "Could not locate a user with the ID: $it"
                    )
                } else {
                    call.application.environment.log.debug("Located user with ID: $it")
                    call.respond(userLookup)
                }
            }
                ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid User ID.") }
        }
        post {
            try {
                val userCreateBody = call.receive<UserDetailCreate>()
                call.application.environment.log.debug(
                    "Parsed requset body to create user with name: ${userCreateBody.name}"
                )
                val createdUser = async { userRepository.createUser(userCreateBody) }.await()
                call.application.environment.log.debug("Created user with ID: ${createdUser?.id}")
                if (createdUser == null) {
                    call.application.environment.log.debug(
                        "Failed to locate created user named: ${userCreateBody.name}"
                    )
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.application.environment.log.debug(
                        "Located created user with ID: ${createdUser.id}"
                    )
                    call.respond(createdUser)
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Unexpected Failure.\n${e.stackTraceToString()}"
                )
            }
        }
        put {
            try {
                val userUpdateBody = call.receive<UserDetailUpdate>()
                val updatedUser = userRepository.updateUser(userUpdateBody)
                if (updatedUser == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(updatedUser)
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
