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

import app.jjerrell.choretender.service.domain.model.user.FamilyMemberDetail

sealed interface FamilyDetail {
    val id: Int?
    val name: String
}

data class FamilyDetailCreate(
    override val id: Int? = null,
    override val name: String,
    val creator: FamilyMemberDetail,
    val invitees: List<FamilyMemberDetail>? = null
) : FamilyDetail

data class FamilyDetailRead(
    override val id: Int,
    override val name: String,
    val members: List<FamilyMemberDetail>
) : FamilyDetail
