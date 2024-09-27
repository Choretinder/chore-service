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

import app.jjerrell.choretender.service.PARAM_CHORE_ID
import app.jjerrell.choretender.service.PARAM_FAMILY_ID
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailCreate
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailUpdate
import app.jjerrell.choretender.service.domain.repository.IChoreServiceChoreRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

internal fun Route.choreRoutes() {
    val choreRepository by inject<IChoreServiceChoreRepository>()

    route("family/{$PARAM_FAMILY_ID}/chore") {
        get {
            try {
                call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                    val familyChores = choreRepository.getFamilyChoreDetails(it)
                    call.respond(familyChores)
                } ?: run { call.respond(HttpStatusCode.BadRequest) }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get("{$PARAM_CHORE_ID}") {
            try {
                call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let { familyId ->
                    call.parameters[PARAM_CHORE_ID]?.toLongOrNull()?.let { choreId ->
                        val requestedChore = choreRepository.getChoreDetail(familyId = familyId, choreId = choreId)
                        requestedChore?.let { call.respond(it) }
                    } ?: run { call.respond(HttpStatusCode.BadRequest) }
                } ?: run { call.respond(HttpStatusCode.BadRequest) }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post {
            try {
                call.parameters[PARAM_FAMILY_ID]?.toLongOrNull()?.let {
                    val choreCreateBody = call.receive<ChoreDetailCreate>()
                    val newChoreDetail = choreRepository.createChore(it, choreCreateBody)
                    newChoreDetail?.let { call.respond(it) }
                } ?: run { call.respond(HttpStatusCode.BadRequest) }
                val familyId = requireNotNull(call.parameters[PARAM_FAMILY_ID])
                call.respondText("Created Chore for family ID: $familyId")
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("update") {
            try {
                val familyId = requireNotNull(call.parameters[PARAM_FAMILY_ID]?.toLongOrNull())
                val choreUpdateBody = call.receive<ChoreDetailUpdate>()
                val updatedChoreDetail = choreRepository.updateChore(familyId, choreUpdateBody)
                updatedChoreDetail?.let { call.respond(it) }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
