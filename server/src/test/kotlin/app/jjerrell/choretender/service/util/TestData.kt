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

import app.jjerrell.choretender.service.domain.model.chore.*
import app.jjerrell.choretender.service.domain.model.family.*
import app.jjerrell.choretender.service.domain.model.user.*

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

    val userDetailCreate =
        UserDetailCreate(
            name = "Test",
            type = UserType.MANAGER,
            contactInfo = null,
            createdBy = 0,
            createdDate = 0
        )

    val userDetailUpdate =
        UserDetailUpdate(
            id = 1,
            name = "Test updated",
            contactInfo =
                ContactInfo(
                    resource = "test@test.dev",
                    type = ContactType.EMAIL,
                    isVerified = true
                ),
            updatedBy = 1,
            updatedDate = 1
        )

    val userDetailUpdateAlternate =
        UserDetailUpdate(
            id = 1,
            name = null,
            type = UserType.STANDARD,
            contactInfo = null,
            updatedBy = 1,
            updatedDate = 1
        )

    val familyDetailCreate =
        FamilyDetailCreate(name = "Test", invitees = null, createdBy = 1, createdDate = 0)

    val familyDetailRead =
        FamilyDetailRead(
            id = 1,
            name = "Test",
            invitees = null,
            members =
                listOf(
                    FamilyMemberDetail(
                        id = 1,
                        memberId = 1,
                        name = "Test",
                        type = UserType.MANAGER,
                        contactInfo = null,
                        invitedBy = 0,
                        invitedDate = 0,
                        isConfirmed = true
                    )
                ),
            createdBy = 1,
            createdDate = 0,
            updatedDate = null,
            updatedBy = null
        )

    val familyDetailInvite = FamilyDetailInvite(inviteeId = 2, invitedBy = 1)

    val familyDetailLeave = FamilyMemberLeave(2)

    val familyMemberVerify = FamilyMemberVerify(memberId = 2)

    val familyMemberPromote = FamilyMemberChangeRole(memberId = 2, role = UserType.MANAGER)

    val choreDetailCreate = ChoreDetailCreate(name = "Test", createdBy = 1, createdDate = 0)

    val choreDetailUpdate =
        ChoreDetailUpdate(id = 1, name = "Test Update", updatedBy = 1, updatedDate = 0)

    val choreDetailReadOne =
        ChoreDetailRead(
            id = 1,
            name = "Test",
            recurrence = ChoreRecurrence.NONE,
            createdBy = 1,
            createdDate = 0,
            endDate = null,
            status = ChoreCompletion.NONE,
            updatedBy = null,
            updatedDate = null
        )

    val choreDetailReadOneUpdated =
        ChoreDetailRead(
            id = 1,
            name = "Test Update",
            recurrence = ChoreRecurrence.NONE,
            createdBy = 1,
            createdDate = 0,
            endDate = null,
            status = ChoreCompletion.NONE,
            updatedBy = 1,
            updatedDate = 1
        )

    val choreDetailReadTwo =
        ChoreDetailRead(
            id = 2,
            name = "Test",
            recurrence = ChoreRecurrence.DAILY,
            createdBy = 1,
            createdDate = 2,
            endDate = null,
            status = ChoreCompletion.NONE,
            updatedBy = null,
            updatedDate = null
        )
}
