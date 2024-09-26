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
package app.jjerrell.choretender.service.domain.model.family

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class FamilyDetailRead(
    val id: Long,
    val name: String,
    val invitees: List<FamilyMemberDetail>? = null,
    val members: List<FamilyMemberDetail>,
    val createdBy: Long,
    val createdDate: Long,
    val updatedDate: Long?,
    val updatedBy: Long?
)

@Serializable
data class FamilyDetailCreate(
    val name: String,
    val invitees: List<Long>? = null,
    val createdBy: Long,
    val createdDate: Long = Clock.System.now().epochSeconds
)

@Serializable data class FamilyDetailInvite(val inviteeId: Long, val invitedBy: Long)
