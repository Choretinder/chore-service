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
package app.jjerrell.choretender.service.util

import app.jjerrell.choretender.service.domain.model.family.*
import app.jjerrell.choretender.service.domain.model.user.UserDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserType

data object TestData {
    val defaultUser =
        UserDetailRead(
            id = 1,
            name = "Test",
            type = UserType.MANAGER,
            contactInfo = null,
            createdBy = 0,
            createdDate = 0,
            updatedDate = null,
            updatedBy = null
        )

    val familyDetailCreate =
        FamilyDetailCreate(name = "Test", invitees = null, createdBy = 1, createdDate = 0)

    val familyDetailInvite = FamilyDetailInvite(inviteeId = 2, invitedBy = 1)

    val familyDetailLeave = FamilyMemberLeave(2)

    val familyMemberVerify = FamilyMemberVerify(memberId = 2)

    val familyMemberPromote = FamilyMemberChangeRole(memberId = 2, role = UserType.MANAGER)
}
