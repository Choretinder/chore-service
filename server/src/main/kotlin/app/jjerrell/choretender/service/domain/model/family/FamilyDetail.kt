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

import app.jjerrell.choretender.service.domain.model.common.ICreatable
import app.jjerrell.choretender.service.domain.model.common.IIdentifiable
import app.jjerrell.choretender.service.domain.model.common.IUpdateable
import app.jjerrell.choretender.service.domain.model.user.FamilyMemberDetail
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

sealed interface FamilyDetail {
    val name: String
}

@Serializable
data class FamilyDetailCreate(
    override val name: String,
    val invitees: List<Long>? = null,
    override val createdBy: Long,
    override val createdDate: Long = Clock.System.now().epochSeconds
) : FamilyDetail, ICreatable

@Serializable
data class FamilyDetailRead(
    override val id: Long,
    override val name: String,
    val invitees: List<FamilyMemberDetail>? = null,
    val members: List<FamilyMemberDetail>,
    override val createdBy: Long,
    override val createdDate: Long,
    override val updatedDate: Long?,
    override val updatedBy: Long?
) : FamilyDetail, IIdentifiable, ICreatable, IUpdateable

@Serializable
data class FamilyDetailInvite(
    override val id: Long,
    val inviteeId: Long,
    val invitedBy: Long,
) : IIdentifiable

@Serializable data class FamilyDetailLeave(override val id: Long, val userId: Long) : IIdentifiable
