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
package app.jjerrell.choretender.service.domain.repository

import app.jjerrell.choretender.service.database.ChoreServiceDatabase
import app.jjerrell.choretender.service.database.entity.FamilyMemberEntity
import app.jjerrell.choretender.service.database.entity.FamilyWithMembers
import app.jjerrell.choretender.service.domain.IChoreServiceFamilyRepository
import app.jjerrell.choretender.service.domain.IChoreServiceUserRepository
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailCreate
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailInvite
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailLeave
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailRead
import app.jjerrell.choretender.service.domain.model.user.FamilyMemberDetail
import app.jjerrell.choretender.service.domain.model.user.UserType
import io.ktor.util.logging.*

internal class FamilyRepository(
    private val db: ChoreServiceDatabase,
    private val userRepository: IChoreServiceUserRepository,
    private val logger: Logger
) : IChoreServiceFamilyRepository {
    override suspend fun getFamilyDetail(id: Long): FamilyDetailRead? {
        return try {
            db.familyDao()
                .getFamilyWithMembers(familyId = id)
                .toFamilyDetail()
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun createFamily(detail: FamilyDetailCreate): FamilyDetailRead? {
        return try {
            throw NotImplementedError("Not yet implemented")
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun inviteFamilyMember(detail: FamilyDetailInvite): FamilyDetailRead? {
        return try {
            throw NotImplementedError("Not yet implemented")
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun leaveFamilyGroup(detail: FamilyDetailLeave): FamilyDetailRead? {
        return try {
            throw NotImplementedError("Not yet implemented")
        } catch (e: Throwable) {
            throw e
        }
    }
}

private fun FamilyWithMembers.toFamilyDetail(): FamilyDetailRead {
    val membersInviteesPair = members.partition { it.isConfirmed }
    return FamilyDetailRead(
        id = family.familyId,
        name = family.familyName,
        invitees = membersInviteesPair.second.map { it.toMemberDetail() },
        members = membersInviteesPair.first.map { it.toMemberDetail() },
        createdBy = family.createdBy,
        createdDate = family.createdDate,
        updatedBy = family.updatedBy,
        updatedDate = family.updatedDate
    )
}

private fun FamilyMemberEntity.toMemberDetail() = FamilyMemberDetail(
    id = user.userId,
    memberId = memberId,
    name = user.name,
    type = UserType.valueOf(user.userType),
    contactInfo = user.contact?.toContactDetail(),
    invitedBy = invitedBy,
    invitedDate = invitedDate,
    isConfirmed = isConfirmed
)
