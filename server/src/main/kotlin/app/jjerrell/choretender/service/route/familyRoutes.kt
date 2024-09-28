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

import app.jjerrell.choretender.service.PARAM_FAMILY_ID
import app.jjerrell.choretender.service.domain.model.family.*
import app.jjerrell.choretender.service.domain.repository.IChoreServiceFamilyRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

internal fun Route.familyRoutes() {
    val familyRepository by inject<IChoreServiceFamilyRepository>()

    route("family") {
        post {
            try {
                val familyCreateBody = call.receive<FamilyDetailCreate>()
                val createdFamily = familyRepository.createFamily(familyCreateBody)
                call.respond(createdFamily)
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid request body")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        route("{$PARAM_FAMILY_ID}") {
            get {
                try {
                    call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                        val familyLookup = familyRepository.getFamilyDetail(it)
                        call.respond(familyLookup)
                    }
                        ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid ID") }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post("invite") {
                try {
                    call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                        val familyInviteBody = call.receive<FamilyDetailInvite>()
                        val updatedFamily =
                            familyRepository.inviteFamilyMember(it, familyInviteBody)
                        call.respond(updatedFamily)
                    }
                        ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid ID") }
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid request body")
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post("leave") {
                try {
                    call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                        val familyLeaveBody = call.receive<FamilyMemberLeave>()
                        val updatedFamily = familyRepository.leaveFamilyGroup(it, familyLeaveBody)
                        call.respond(updatedFamily)
                    }
                        ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid ID") }
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid request body")
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post("verify") {
                try {
                    call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                        val familyVerifyBody = call.receive<FamilyMemberVerify>()
                        val updatedFamily =
                            familyRepository.verifyFamilyMember(it, familyVerifyBody)
                        call.respond(updatedFamily)
                    }
                        ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid ID") }
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid request body")
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post("role") {
                try {
                    call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                        val familyMemberRoleBody = call.receive<FamilyMemberChangeRole>()
                        val updatedFamily =
                            familyRepository.changeMemberRole(it, familyMemberRoleBody)
                        call.respond(updatedFamily)
                    }
                        ?: run { call.respond(HttpStatusCode.BadRequest, "Missing or invalid ID") }
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid request body")
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
