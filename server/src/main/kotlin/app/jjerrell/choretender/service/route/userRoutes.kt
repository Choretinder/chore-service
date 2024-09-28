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

import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate
import app.jjerrell.choretender.service.domain.repository.IChoreServiceUserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import org.koin.ktor.ext.inject

internal fun Route.userRoutes() {
    val userRepository by inject<IChoreServiceUserRepository>()

    route("user") {
        get("{id?}") {
            try {
                call.parameters["id"]?.toLongOrNull()?.let {
                    val userLookup = userRepository.getUserDetail(it)
                    if (userLookup == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(userLookup)
                    }
                }
                    ?: run { call.respond(HttpStatusCode.BadRequest) }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post {
            try {
                val userCreateBody = call.receive<UserDetailCreate>()
                val createdUser = userRepository.createUser(userCreateBody)
                if (createdUser == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(createdUser)
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
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
