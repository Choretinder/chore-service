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

import app.jjerrell.choretender.service.domain.IChoreServiceFamilyRepository
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailCreate
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailInvite
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailLeave
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import org.koin.ktor.ext.inject

internal fun Routing.familyRoutes() {
    val familyRepository by inject<IChoreServiceFamilyRepository>()

    route("family") {
        get("{id?}") {
            try {
                call.parameters["id"]?.toLongOrNull()?.let {
                    val familyLookup = familyRepository.getFamilyDetail(it)
                    if (familyLookup == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            "Could not locate a family with the ID: $it"
                        )
                    } else {
                        call.respond(familyLookup)
                    }
                }
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing or invalid family ID.")
                    }
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Unexpected Failure.\n${e.stackTraceToString()}"
                )
            }
        }
        post {
            try {
                val familyCreateBody = call.receive<FamilyDetailCreate>()
                val createdFamily =
                    async { familyRepository.createFamily(familyCreateBody) }.await()
                if (createdFamily == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(createdFamily)
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
        post("invite") {
            try {
                val familyInviteBody = call.receive<FamilyDetailInvite>()
                val updatedFamily = familyRepository.inviteFamilyMember(familyInviteBody)
                if (updatedFamily == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(updatedFamily)
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
        post("leave") {
            try {
                val familyLeaveBody = call.receive<FamilyDetailLeave>()
                val updatedFamily = familyRepository.leaveFamilyGroup(familyLeaveBody)
                if (updatedFamily == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(updatedFamily)
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
    }
}
